package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.constant.GameStatus;
import com.wyc.bgswitch.redis.entity.User;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.List;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.SIT)
public class SitActionHandler implements ActionHandler {
    @Override
    public Boolean check(CitadelGame game, CitadelGameAction action, User user) {
        return checkStatus(game) && checkPlayer(game, user) && checkPlayerNumber(game);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, User user) {
        game.getPlayers().add(new CitadelPlayer(user.getId()));
        return game;
    }


    /**
     * 准备阶段可以进入
     *
     * @param game
     * @return
     */
    private Boolean checkStatus(CitadelGame game) {
        return GameStatus.PREPARE.equals(game.getStatus());
    }

    /**
     * 玩家是否已经坐下
     *
     * @param game
     * @param user
     * @return
     */
    private Boolean checkPlayer(CitadelGame game, User user) {
        List<CitadelPlayer> playerList = game.getPlayers();
        return playerList.stream().map(CitadelPlayer::getUserId).toList().contains(user.getId());
    }

    /**
     * 玩家超出上限
     *
     * @param game
     * @return
     */
    private Boolean checkPlayerNumber(CitadelGame game) {
        return game.getPlayers().size() < game.getConfig().getPlayerNumber();
    }

}
