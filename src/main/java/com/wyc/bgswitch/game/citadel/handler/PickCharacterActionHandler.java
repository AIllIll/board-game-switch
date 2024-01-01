package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.judge.JudgeManager;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.PICK_CHARACTER)
public class PickCharacterActionHandler implements ActionHandler {

    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToPick(game, userId);
        Integer characterIdx = JSON.parseObject(action.getBody(), Integer.class);
        ActionAssertUtil.assertCharacterAvailable(game, characterIdx);
    }

    /**
     * @param game
     * @param action
     * @param userId
     * @return
     */
    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        Integer characterIdx = JSON.parseObject(action.getBody(), Integer.class);
        CitadelGameCharacter character = CitadelGameCharacter.values()[characterIdx];
        int turn = game.getTurn();
        int playerNumber = game.getPlayers().size();
        // start counting from crown
        int currentPlayerIdx = (game.getCrown() + turn) % playerNumber;
        CitadelPlayer player = game.getPlayers().get(currentPlayerIdx);
        if (turn < playerNumber) {
            // first turn
            player.setCharacters(new ArrayList<>());
        }
        player.getCharacters().add(character); // add
        player.getCharacters().sort(CitadelGameCharacter::compareTo); // sort
        game.getCharacterCardStatus().set(characterIdx, CitadelGameCharacter.CardStatus.PICKED);
        JudgeManager.afterPickingTurn(game);
        return game;
    }
}
