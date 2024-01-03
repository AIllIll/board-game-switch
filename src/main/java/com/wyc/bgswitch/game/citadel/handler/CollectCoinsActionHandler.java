package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.judge.JudgeManager;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.COLLECT_COINS)
public class CollectCoinsActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerNotCollected(game);
        ActionAssertUtil.assertPlayerNotCollecting(game);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        CitadelPlayer player = game.getCurrentPlayer();
        player.setCoins(player.getCoins() + 2);
        player.getStatus().setCollected(true);

        // after action
        JudgeManager.afterAction(game);
        // after move
        JudgeManager.afterMove(game);
        return game;
    }
}
