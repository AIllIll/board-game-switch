package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.List;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.SIT)
public class SitActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        Integer seatIdx = JSON.parseObject(action.getBody(), Integer.class);
        ActionAssertUtil.assertStatusPrepare(game);
        ActionAssertUtil.assertSeatAvailable(game, seatIdx);
        ActionAssertUtil.assertPlayerNotFull(game);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        Integer seatIdx = JSON.parseObject(action.getBody(), Integer.class);
        List<CitadelPlayer> playerList = game.getPlayers();
        int formerIdx = playerList.stream().map(CitadelPlayer::getUserId).toList().indexOf(userId);
        if (formerIdx >= 0) {
            // 对应换座的情况
            playerList.set(formerIdx, CitadelPlayer.emptyPlayer());
        }
        playerList.set(seatIdx, new CitadelPlayer(userId));

        return game;
    }

}
