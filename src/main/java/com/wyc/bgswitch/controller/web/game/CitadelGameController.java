package com.wyc.bgswitch.controller.web.game;

import com.wyc.bgswitch.config.lock.RedisLockPrefix;
import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.citadel.CitadelGameService;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.handler.CitadelGameActionHandlerManager;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.constant.GameStatus;
import com.wyc.bgswitch.game.exception.CreateGameConflictException;
import com.wyc.bgswitch.game.exception.GameNotFoundException;
import com.wyc.bgswitch.lock.LockManager;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;
import com.wyc.bgswitch.service.RoomService;
import com.wyc.bgswitch.service.message.GameMessageService;
import com.wyc.bgswitch.service.message.RoomMessageService;
import com.wyc.bgswitch.utils.debug.Debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/game/citadel")
public class CitadelGameController {
    private final RoomService roomService;
    private final CitadelGameService gameService;
    private final LockManager lockManager;
    private final CitadelGameActionHandlerManager handlerManager;

    private final GameMessageService gameMessageService;
    private final RoomMessageService roomMessageService;

    @Autowired
    public CitadelGameController(RoomService roomService, CitadelGameService gameService, LockManager lockManager, CitadelGameActionHandlerManager handlerManager, GameMessageService gameMessageService, RoomMessageService roomMessageService) {
        this.roomService = roomService;
        this.gameService = gameService;
        this.lockManager = lockManager;
        this.handlerManager = handlerManager;
        this.gameMessageService = gameMessageService;
        this.roomMessageService = roomMessageService;
    }

    @Debug
    @PostMapping("/create")
    public String createGame(@RequestBody CreateGameRequestBody body, Authentication authentication) {
        String roomId = body.roomId();
        String lastGameId = roomService.getLastGameId(roomId);
        CitadelGame game = gameService.get(lastGameId);
        if (game != null && game.getStatus() != GameStatus.END) {
            throw new CreateGameConflictException();
        }
        String gameIdWithPrefix = gameService.create(roomId, new CitadelGameConfig(body.playerNumber), authentication.getName());
        roomService.attachGameToRoom(roomId, gameIdWithPrefix);
        roomMessageService.notifyUpdate(roomService.getRoomInfo(roomId));
        return gameIdWithPrefix;
    }

    @Debug
    @GetMapping("/{gameId}")
//    @JsonView(CitadelGame.FrontendView.class)
    public CitadelGame getGame(@PathVariable String gameId) {
        return gameService.get(gameId);
    }

    @Debug
    @PostMapping("/action/{gameId}")
    public void action(@PathVariable String gameId, @RequestBody CitadelGameAction action, Authentication authentication) {
        LockManager.MultiLockBuilder.MultiLock lock = lockManager.useBuilder().obtain(RedisLockPrefix.LOCK_PREFIX_GAME).of(gameId).build();
        lock.lock();
        try {
            CitadelGame game = gameService.get(gameId);
            if (game == null) {
                throw new GameNotFoundException();
            }
            CitadelGame newGame = handlerManager.handleAction(game, action, authentication.getName());
            gameService.update(gameId, newGame);
            gameMessageService.notifyUpdate(roomService.getRoomUserIds(game.getRoomId()), newGame);
        } finally {
            lock.unLock();
        }
    }

    /**
     * <a href="http://localhost:8080/api/game/citadel/constant">...</a>
     *
     * @return
     * @throws ClassNotFoundException
     */
    @GetMapping("/constant")
    public List<String> constant() throws ClassNotFoundException {
        return List.of(
                DistrictCard.DistrictCardType.toFrontendConstantObject(),
                DistrictCard.toFrontendConstantObject(),
                CitadelGameCharacter.toFrontendConstantObject()
        );
    }

    public record CreateGameRequestBody(String roomId, Integer playerNumber) {
    }


}
