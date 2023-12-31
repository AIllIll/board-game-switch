package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.BUILD)
public class BuildActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerCollected(game);
        ActionAssertUtil.assertCanBuild(game, action);

    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        int handIdx = JSON.parseObject(action.getBody(), Integer.class); // idx in hand
        int cardId = game.getCurrentPlayer().getHand().get(handIdx);
        DistrictCard card = DistrictCard.values()[cardId];
        CitadelPlayer player = game.getCurrentPlayer();
        player.getHand().remove(handIdx); // remove from hand
        if (player.getDistricts() == null) {
            player.setDistricts(new ArrayList<>());
        }
        player.getDistricts().add(cardId); // add to districts
        player.getDistricts().sort(Integer::compareTo); // add to districts
        player.setCoins(player.getCoins() - card.getCost()); // cost coins
        player.getStatus().costBuildTimes(); // cost build times
        return game;
    }
}
