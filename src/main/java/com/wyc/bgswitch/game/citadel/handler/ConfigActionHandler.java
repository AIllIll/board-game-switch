package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson.JSONObject;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.constant.GameStatus;
import com.wyc.bgswitch.redis.entity.User;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.List;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.CONFIG)
public class ConfigActionHandler implements ActionHandler {
    @Override
    public Boolean check(CitadelGame game, CitadelGameAction action, User user) {
        return checkStatus(game) && checkHost(game, user) && checkPlayerNumber(game, action);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, User user) {
        CitadelGameConfig config = JSONObject.parseObject(action.getBody(), CitadelGameConfig.class);
        game.setConfig(config);
        return game;
    }

    /**
     * 准备阶段可以改
     *
     * @param game
     * @return
     */
    private Boolean checkStatus(CitadelGame game) {
        return GameStatus.PREPARE.equals(game.getStatus());
    }

    /**
     * 玩家是否房主
     *
     * @param game
     * @param user
     * @return
     */
    private Boolean checkHost(CitadelGame game, User user) {
        List<CitadelPlayer> playerList = game.getPlayers();
        return playerList.stream().map(CitadelPlayer::getUserId).toList().contains(user.getId());
    }

    /**
     * 玩家超出上限
     *
     * @param game
     * @return
     */
    private Boolean checkPlayerNumber(CitadelGame game, CitadelGameAction action) {
        CitadelGameConfig config = JSONObject.parseObject(action.getBody(), CitadelGameConfig.class);
        return game.getPlayers().size() <= config.getPlayerNumber();
    }

}
