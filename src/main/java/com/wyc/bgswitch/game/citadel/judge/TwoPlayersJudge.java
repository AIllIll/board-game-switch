package com.wyc.bgswitch.game.citadel.judge;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        // 1. hide 1 character for second player to pick at last
        List<CitadelGameCharacter.CardStatus> statusList = game.getCharacterCardStatus();
        Random random = new Random(game.getRandomSeed());
        int hiddenIdx = random.nextInt(statusList.size());
        statusList.set(hiddenIdx, CitadelGameCharacter.CardStatus.HIDDEN);
    }

    /**
     * 1. bury 1 character
     * 2. show the hidden character for the last player to pick (when next player is the last)
     *
     * @param game
     */
    @Override
    public void afterPickingTurn(CitadelGame game) {
        List<CitadelGameCharacter.CardStatus> statusList = game.getCharacterCardStatus();
        // 1. randomly bury 1 character
        List<Integer> availableCharacters = new ArrayList<>();
        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).equals(CitadelGameCharacter.CardStatus.AVAILABLE)) {
                availableCharacters.add(i);
            }
        }
        Random random = new Random(game.getRandomSeed());
        int removeIdx = availableCharacters.get(random.nextInt(availableCharacters.size()));
        statusList.set(removeIdx, CitadelGameCharacter.CardStatus.BURIED);
        // 2. show the hidden character
        int lastPickingTurn = 2 * game.getPlayers().size() - 1;
        if (game.getPickingTurn() == lastPickingTurn - 1) {
            // the next player is lastPickingTurn, which is the last player
            // this is actually doBeforePickingTurn
            for (int i = 0; i < statusList.size(); i++) {
                if (statusList.get(i).equals(CitadelGameCharacter.CardStatus.HIDDEN)) {
                    statusList.set(i, CitadelGameCharacter.CardStatus.AVAILABLE);
                }
            }
        }
        super.afterPickingTurn(game);
    }
}
