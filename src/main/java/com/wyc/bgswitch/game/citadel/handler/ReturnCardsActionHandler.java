package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson2.JSON;
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
@Handler(CitadelGameActionType.RETURN_CARDS)
public class ReturnCardsActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerCollecting(game);
        ActionAssertUtil.assertDrawnCardNotEmpty(game);
        ActionAssertUtil.assertKeepingCardNumber(game, action);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        List<Integer> keptCardIdxes = JSON.parseArray(action.getBody(), Integer.class);
        CitadelPlayer player = game.getCurrentPlayer();
        List<Integer> cardDeck = game.getCardDeck();
        for (int idx = 0; idx < player.getDrawnCards().size(); idx++) {
            if (keptCardIdxes.contains(idx)) {
                // cards chosen
                player.getHand().add(player.getDrawnCards().get(idx));
            } else {
                // cards not chosen, put them back
                cardDeck.add(player.getDrawnCards().get(idx));
            }
        }
        player.setDrawnCards(null); // clear list
        player.getStatus().setCollecting(false);
        player.getStatus().setCollected(true);
        return game;
    }
}