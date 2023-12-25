package com.wyc.bgswitch.controller.web.game;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.citadel.CitadelGameService;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/game/citadel")
public class CitadelGameController {
    private final RoomService roomService;
    private final CitadelGameService gameService;

    @Autowired
    public CitadelGameController(RoomService roomService, CitadelGameService gameService) {
        this.roomService = roomService;
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public String createGame(@RequestBody CreateGameRequestBody body, Authentication authentication) {
        String roomId = body.roomId();
        String gameIdWithPrefix = gameService.create(roomId, body.config, authentication.getName());
        roomService.attachGameToRoom(roomId, gameIdWithPrefix);
        return gameIdWithPrefix;
    }

    @GetMapping("/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "game: " + gameId;
    }


    public record CreateGameRequestBody(String roomId, CitadelGameConfig config) {
    }

}
