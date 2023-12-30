package com.wyc.bgswitch.game.citadel.judge;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
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
     * 1. initialize card deck
     * 2. initialize player hand
     * 3. randomly pick initial heir
     * (crown will be given to heir before round start)
     * 4. initialize player score
     * 5. initialize round
     * 6. initialize firstFinishedPlayer
     * 7. initialize turn
     * 8. initialize characterCardStatus
     * 9. initialize characterStatus
     * 10. initialize coins
     *
     * @param game game
     */
    public final void beforeGame(CitadelGame game) {
        // use createdAt as random seed
        Random random = new Random(game.getRandomSeed());
        // 1. initialize card deck
        List<Integer> cards = DistrictCard.getAll();
        Collections.shuffle(cards, random);
        game.setCardDeck(cards);
        // 2. initialize player hand
        for (CitadelPlayer player : game.getPlayers()) {
            player.setHand(new ArrayList<>());
            // take 4 cards from card deck
            player.getHand().addAll(cards.subList(0, 4));
            // remove 4 cards from card deck
            game.getCardDeck().subList(0, 4).clear();
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
        game.setTurn(0);
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
            p.setCharacters(null);
        });
        // 3.
        game.setRound(game.getRound() + 1);
        // 4.
        game.setTurn(0);
        // 5.
        game.clearCharacterCardStatus();
        // 6.
        game.clearCharacterStatus();
    }

    /**
     * 1. increase turn
     * 2. start character turn after last player pick his last character
     *
     * @param game
     */
    @OverridingMethodsMustInvokeSuper
    public void afterPickingTurn(CitadelGame game) {
        // 1. increase pickingTurn
        game.setTurn(game.getTurn() + 1);
        // 2. start character turn after last player pick his last character
        if (game.isInCharacterTurn()) {
            int i = game.getTurn() - game.getPlayers().size() * 2;
            for (; i < 8; i++) {
                if (game.getCharacterCardStatus().get(i).equals(CitadelGameCharacter.CardStatus.PICKED)
                        && !game.getCharacterStatus().get(i).isOver()) {
                    game.setTurn(i + game.getPlayers().size() * 2);
                    break;
                }
            }
        }
    }

    /**
     * update score for every player
     * (some moves might change other players' score)
     *
     * @param game
     */
    @OverridingMethodsMustInvokeSuper
    public final void afterMove(CitadelGame game) {
        game.getPlayers().forEach(p -> {
            p.setScore(ScoreUtil.computeScore(p)); // todo: hide score from other players
            p.setVisibleScore(ScoreUtil.computeScore(p)); // todo: check computation logic
        });
    }

    /**
     * 1. clear player status
     * 2. mark current character status as over
     * 3. increase turn to next character
     * 4. do afterRound if all turns are over
     *
     * @param game
     */
    @OverridingMethodsMustInvokeSuper
    public void afterCharacterTurn(CitadelGame game) {
        // 1. clear player status
        game.getCurrentPlayer().resetStatus();
        // 2. mark current character status as over
        int i = game.getTurn() - game.getPlayers().size() * 2;
        game.getCharacterStatus().get(i).setOver(true);
        // 3. increase turn to next character
        for (; i < 8; i++) {
            if (game.getCharacterCardStatus().get(i).equals(CitadelGameCharacter.CardStatus.PICKED)
                    && !game.getCharacterStatus().get(i).isOver()) {
                break;
            }
        }
        game.setTurn(i + game.getPlayers().size() * 2);
        // 4. do afterRound if all turns are over
        if (i == 8) {
            // all turns are over
            JudgeManager.afterRound(game);
            JudgeManager.beforeRound(game);
        }
    }

    /**
     * 1. check score and announce winner
     *
     * @param game
     */
    public void afterRound(CitadelGame game) {
        // 1. check score and announce winner
        if (game.getFirstFinishedPlayer() >= 0) {
            game.setFinishedAt(new Date().getTime());
        }
    }
}
