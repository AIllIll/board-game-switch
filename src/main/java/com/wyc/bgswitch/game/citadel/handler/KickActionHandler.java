package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.check.ActionAssertUtil;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
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
        Integer seatIdx = JSON.parseObject(action.getBody(), Integer.class);
        ActionAssertUtil.assertIsHost(game, userId);
        ActionAssertUtil.assertStatusPrepare(game);
        ActionAssertUtil.assertSeatTaken(game, seatIdx);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        Integer seatIdx = JSON.parseObject(action.getBody(), Integer.class);

        List<CitadelPlayer> playerList = new ArrayList<>(game.getPlayers());
        playerList.set(seatIdx, CitadelPlayer.emptyPlayer());
        game.setPlayers(playerList);
        
        return game;
    }
}
