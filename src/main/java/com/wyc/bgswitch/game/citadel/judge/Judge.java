package com.wyc.bgswitch.game.citadel.judge;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ScoreUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author wyc
 * <p>
 * perform something actions that players are not involved
 */

public abstract class Judge {
    /**
     * 1. initialize hand
     * 2. initialize card deck
     * 3. randomly pick initial heir
     * (crown will be given to heir before round start)
     * 4. initialize player score
     * 5. initialize round
     * 6. initialize firstFinishedPlayer
     * 7. initialize pickingTurn
     * 8. initialize characterCardStatus
     * 9. initialize characterStatus
     * 10. initialize coins
     *
     * @param game game
     */
    public final void beforeGame(CitadelGame game) {
        // use createdAt as random seed
        Random random = new Random(game.getRandomSeed());
        // 1.card deck
        List<Integer> cards = DistrictCard.getAll();
        Collections.shuffle(cards, random);
        game.setCardDeck(cards);
        // 2.hand
        for (CitadelPlayer player : game.getPlayers()) {
            player.setHand(new ArrayList<>());
            // take 4 cards from card deck
            player.getHand().addAll(cards.subList(0, 4));
            // remove 4 cards from card deck
            cards = cards.subList(4, cards.size());
        }
        // 3.heir
        game.setHeir(random.nextInt(game.getPlayers().size()));
        // 4.score
        game.getPlayers().forEach(p -> {
            p.setScore(0);
            p.setVisibleScore(0);
        });
        // 5.round
        game.setRound(0);
        // 6.firstFinishedPlayer
        game.setFirstFinishedPlayer(-1);
        // 7.initialize pickingTurn
        game.setPickingTurn(-1);
        // 8.initialize characterCardStatus
        game.clearCharacterStatus();
        // 9.initialize characterStatus
        game.clearCharacterCardStatus();
        // 10. initialize coins
        game.getPlayers().forEach(p -> p.setCoins(2));
    }

    /**
     * 1. give crown to heir
     * 2. clean player characters
     * 3. increase round number
     * 4. set pickingTurn
     * 5. initialize characterCardStatus
     * 6. initialize characterStatus
     *
     * @param game
     */
    @OverridingMethodsMustInvokeSuper
    public void beforeRound(CitadelGame game) {
        // 1.
        // we don't clean heir, heir will only be changed when a new King reveal his identity
        game.setCrown(game.getHeir());
        // 2.
        game.getPlayers().forEach(p -> {
            p.setCharacter1(null);
            p.setCharacter2(null);
        });
        // 3.
        game.setRound(game.getRound() + 1);
        // 4.
        game.setPickingTurn(0);
        // 5.
        game.clearCharacterCardStatus();
        // 6.
        game.clearCharacterStatus();
    }

    /**
     * 1. increase pickingTurn
     * 2. start character turn after last player pick his last character
     *
     * @param game
     */
    @OverridingMethodsMustInvokeSuper
    public void afterPickingTurn(CitadelGame game) {
        // 1. increase pickingTurn
        game.setPickingTurn(game.getPickingTurn() + 1);
        // 2. start character turn after last player pick his last character

    }

    /**
     * update score for every player
     * (some moves might change other players' score)
     *
     * @param game
     */
    public final void afterMove(CitadelGame game) {
        game.getPlayers().forEach(p -> {
            p.setScore(ScoreUtil.computeScore(p)); // todo: hide score from other players
            p.setVisibleScore(ScoreUtil.computeScore(p)); // todo: check computation logic
        });
    }

    /**
     * check score and announce winner
     *
     * @param game
     */
    public void afterRound(CitadelGame game) {
        if (game.getFirstFinishedPlayer() >= 0) {
            game.setFinishedAt(new Date().getTime());
        }
    }
}
