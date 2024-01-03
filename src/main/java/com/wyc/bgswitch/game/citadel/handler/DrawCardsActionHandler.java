package com.wyc.bgswitch.game.citadel.handler;

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
@Handler(CitadelGameActionType.DRAW_CARDS)
public class DrawCardsActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerNotCollected(game);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        CitadelPlayer player = game.getCurrentPlayer();
        List<Integer> cardDeck = game.getCardDeck();
        int drawNum = player.getStatus().getDraw();
        player.setDrawnCards(new ArrayList<>(cardDeck.subList(0, drawNum)));
        cardDeck.subList(0, drawNum).clear();
        player.getStatus().setCollecting(true);
        return game;
    }
}