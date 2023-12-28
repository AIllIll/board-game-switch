package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.citadel.judge.JudgeManager;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.game.constant.GameStatus;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.Date;

/**
 * @author wyc
 */
public class StartActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusPrepare(game);
        ActionAssertUtil.assertPlayerFull(game);
        ActionAssertUtil.assertIsHost(game, userId);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        game.setStatus(GameStatus.ONGOING);
        game.setStartedAt(new Date().getTime());
        JudgeManager.beforeGame(game);
        JudgeManager.beforeRound(game);
        return game;
    }
}
