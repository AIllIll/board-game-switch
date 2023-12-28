package com.wyc.bgswitch.game.citadel.judge;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
public class TwoPlayersJudge extends Judge {

    /**
     * 1. hide 1 character for second player to pick at last
     *
     * @param game
     */
    @Override
    public void beforeRound(CitadelGame game) {
        super.beforeRound(game);
    }

    /**
     * 1. bury 1 character
     *
     * @param game
     */
    @Override
    public void afterTurn(CitadelGame game) {

    }
}
