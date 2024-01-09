package com.wyc.bgswitch.game.citadel.judge;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
public class JudgeManager {
    private static final Judge JUDGE_FOR_TWO = new TwoPlayersJudge();

    private static Judge getJudge(CitadelGame game) {
        if (game.getConfig().getPlayerNumber().equals(2)) {
            return JUDGE_FOR_TWO;
        } else {
            throw new RuntimeException("Judge not found.");
        }
    }

    public static void beforeGame(CitadelGame game) {
        getJudge(game).beforeGame(game);
    }

    public static void beforeRound(CitadelGame game) {
        getJudge(game).beforeRound(game);
    }

    public static void afterPickingTurn(CitadelGame game) {
        getJudge(game).afterPickingTurn(game);
    }

    public static void afterCharacterTurn(CitadelGame game) {
        getJudge(game).afterCharacterTurn(game);
    }

    public static void afterExtraTurn(CitadelGame game) {
        getJudge(game).afterExtraTurn(game);
    }

    public static void afterAction(CitadelGame game) {
        getJudge(game).afterAction(game);
    }

    public static void afterMove(CitadelGame game) {
        getJudge(game).afterMove(game);
    }

    public static void afterRound(CitadelGame game) {
        getJudge(game).afterRound(game);
    }
}
