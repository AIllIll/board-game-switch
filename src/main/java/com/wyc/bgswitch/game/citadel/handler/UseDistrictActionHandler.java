package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.game.exception.ActionUnavailableException;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.ArrayList;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.USE_DISTRICT)
public class UseDistrictActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerCollected(game);
        UseDistrictActionBody body = JSON.parseObject(action.getBody(), UseDistrictActionBody.class);
        ActionAssertUtil.assertCanUseDistrict(game, body.district);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        UseDistrictActionBody body = JSON.parseObject(action.getBody(), UseDistrictActionBody.class);
        DistrictCard district = body.district();
        if (DistrictCard.Laboratory.equals(district)) {
            CitadelPlayer player = game.getCurrentPlayer();
            if (game.getCurrentPlayer().getHand() == null || game.getCurrentPlayer().getHand().size() == 0) {
                throw new ActionUnavailableException("Your hand is empty.");
            }
            player.getHand().remove(body.laboratoryAbilityBody.districtCardId);
            player.setCoins(player.getCoins() + 1);

        } else if (DistrictCard.Smithy.equals(district)) {
            CitadelPlayer player = game.getCurrentPlayer();
            if (player.getCoins() <= 2) {
                throw new ActionUnavailableException("Your don't have enough coins to trade.");
            }
            if (player.getHand() == null) {
                player.setHand(new ArrayList<>());
            }
            // 扣钱
            player.setCoins(player.getCoins() - 2);
            // 抽卡
            player.getHand().addAll(game.getCardDeck().subList(0, 3));
            game.getCardDeck().subList(0, 3).clear();
        } else {
            throw new ActionUnavailableException("The district does not have special function.");
        }
        CitadelPlayer.Status status = game.getCurrentPlayer().getStatus();
        status.getUsedDistricts().add(district);
        return game;
    }

    private record UseDistrictActionBody(DistrictCard district, LaboratoryAbilityBody laboratoryAbilityBody) {
    }

    private record LaboratoryAbilityBody(int districtCardId) {
    }
}
