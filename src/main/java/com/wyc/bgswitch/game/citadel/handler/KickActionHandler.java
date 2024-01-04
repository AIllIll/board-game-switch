package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.KICK)
public class KickActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        String playerId = JSON.parseObject(action.getBody(), String.class);
        ActionAssertUtil.assertIsHost(game, userId);
        ActionAssertUtil.assertStatusPrepare(game);
        ActionAssertUtil.assertPlayerIsSitting(game, playerId);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        String playerId = JSON.parseObject(action.getBody(), String.class);
        List<CitadelPlayer> playerList = new ArrayList<>(game.getPlayers());
        for (int seatIdx = 0; seatIdx < playerList.size(); seatIdx++) {
            if (playerId.equals(playerList.get(seatIdx).getUserId())) {
                playerList.set(seatIdx, CitadelPlayer.emptyPlayer());
            }
        }
        game.setPlayers(playerList);

        return game;
    }
}
