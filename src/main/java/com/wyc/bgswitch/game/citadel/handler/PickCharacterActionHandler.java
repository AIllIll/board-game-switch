package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.PICK_CHARACTER)
public class PickCharacterActionHandler implements ActionHandler {

    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToPick(game, userId);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        Integer idx = JSON.parseObject(action.getBody(), Integer.class);
        CitadelGameCharacter character = CitadelGameCharacter.values()[idx];
        int turn = game.getPickingTurn();
        int playerNumber = game.getPlayers().size();
        // start counting from crown
        int currentPlayerIdx = (game.getCrown() + turn) % playerNumber;
        CitadelPlayer player = game.getPlayers().get(currentPlayerIdx);
        if (turn < playerNumber) {
            // first turn
            player.setCharacter1(character);
        } else {
            // second turn
            player.setCharacter2(character);
        }
        return game;
    }
}
