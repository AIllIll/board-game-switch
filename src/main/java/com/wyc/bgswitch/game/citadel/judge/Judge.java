package com.wyc.bgswitch.game.citadel.judge;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 * <p>
 * perform something actions that players are not involved
 */
public abstract class Judge {
    /**
     * 1. initialize hand
     * 2. initialize card deck
     * 3. randomly pick initial crown
     *
     * @param game game
     */
    public void beforeGame(CitadelGame game) {

    }

    /**
     * @param game
     */
    abstract void beforeRound(CitadelGame game);

    /**
     * @param game
     */
    abstract void afterTurn(CitadelGame game);

    /**
     * check score and announce winner
     *
     * @param game
     */
    public void afterRound(CitadelGame game) {

    }
}
