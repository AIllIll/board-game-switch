package com.wyc.bgswitch.game.citadel.handler;


import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.judge.JudgeManager;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.game.exception.ActionUnavailableException;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import org.bouncycastle.util.Arrays;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.USE_ABILITY)
public class UseAbilityActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusOngoing(game);
        ActionAssertUtil.assertCorrectTurnToMove(game, userId);
        ActionAssertUtil.assertPlayerCollected(game);
        UseAbilityActionBody body = JSON.parseObject(action.getBody(), UseAbilityActionBody.class);
        ActionAssertUtil.assertCanUseAbility(game, body.ability);
    }

    /**
     * todo: 重构代码
     *
     * @param game
     * @param action
     * @param userId
     * @return
     */
    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        int currentCharacterIdx = game.getCurrentCharacterIdx();
        CitadelGameCharacter character = CitadelGameCharacter.values()[currentCharacterIdx];
        UseAbilityActionBody body = JSON.parseObject(action.getBody(), UseAbilityActionBody.class);
        CitadelGameCharacter.Ability ability = body.ability;
        if (CitadelGameCharacter.Ability.Assassinate.equals(ability)) {
            // 目标角色
            int targetCharacterIdx = body.assassinateAbilityParam.characterIdx;
            if (targetCharacterIdx == 0) {
                throw new ActionUnavailableException("Assassin won't commit suicide.");
            }
            if (targetCharacterIdx >= 8 || targetCharacterIdx < 0) {
                throw new ActionUnavailableException("Invalid target for assassination.");
            }
            // 标记被杀
            game.getCharacterStatus().get(targetCharacterIdx).setAssassinated(true);
        } else if (CitadelGameCharacter.Ability.Steal.equals(ability)) {
            // 目标角色
            int targetCharacterIdx = body.stealAbilityParam.characterIdx;
            // 如果目标角色是杀手
            if (targetCharacterIdx == 0 || targetCharacterIdx == 1) {
                throw new ActionUnavailableException("Can't steal from assassin or yourself.");
            }
            // 如果目标角色被杀
            CitadelGameCharacter.InGameStatus status = game.getCharacterStatus().get(targetCharacterIdx);
            if (status.isAssassinated()) {
                throw new ActionUnavailableException("Don't steal from the dead.");
            }
            // 标记被偷
            game.getCharacterStatus().get(targetCharacterIdx).setStolen(true);
        } else if (CitadelGameCharacter.Ability.Magic.equals(ability)) {
            CitadelPlayer currentPlayer = game.getCurrentPlayer();
            int option = body.magicAbilityParam.option;
            // 魔术师有两个技能，2选1
            if (option == 0) {
                // 交换手牌
                String targetPlayerId = body.magicAbilityParam.playerId;
                game.getPlayers().forEach(p -> {
                    if (p.getUserId().equals(targetPlayerId)) {
                        List<Integer> hisCardList = p.getHand();
                        p.setHand(currentPlayer.getHand());
                        currentPlayer.setHand(hisCardList);
                    }
                });
            } else if (option == 1) {
                // 重铸
                int[] discardList = body.magicAbilityParam.discardList;
                List<Integer> drawCards = game.getCardDeck().subList(0, discardList.length); // todo： 如果抽爆了要洗牌
                List<Integer> hand = currentPlayer.getHand();
                // 因为按idx删除的时候无法保序号
                List<Integer> newHand = new ArrayList<>();
                for (int i = 0; i < hand.size(); i++) {
                    if (!Arrays.contains(discardList, i)) {
                        newHand.add(hand.get(i));
                    }
                }
                newHand.addAll(drawCards); // 获取新卡
                newHand.sort(Integer::compareTo);
                currentPlayer.setHand(newHand);
                drawCards.clear();
            } else {
                throw new ActionUnavailableException("No such magic option.");
            }
        } else if (CitadelGameCharacter.Ability.Collect.equals(ability)) {
            if (CitadelGameCharacter.KING.equals(character)) {
                handleCollect(game, DistrictCard.DistrictCardType.Noble);
            } else if (CitadelGameCharacter.BISHOP.equals(character)) {
                handleCollect(game, DistrictCard.DistrictCardType.Religious);
            } else if (CitadelGameCharacter.MERCHANT.equals(character)) {
                handleCollect(game, DistrictCard.DistrictCardType.Trade);
            } else if (CitadelGameCharacter.WARLORD.equals(character)) {
                handleCollect(game, DistrictCard.DistrictCardType.Military);
            }
        } else if (CitadelGameCharacter.Ability.Destroy.equals(ability)) {
            // 摧毁
            String targetPlayerId = body.destroyAbilityParam.playerId;
            CitadelPlayer targetPlayer = game.getPlayers().stream()
                    .filter(p -> targetPlayerId.equals(p.getUserId())).findAny().orElse(null);
            if (targetPlayer == null) {
                throw new ActionUnavailableException("Invalid player id.");
            }
            if (targetPlayer.getCharacters().contains(CitadelGameCharacter.BISHOP)) {
                throw new ActionUnavailableException("The district is in bishop's protection.");
            }
            int districtCardId = body.destroyAbilityParam.districtCardId;
            if (districtCardId == DistrictCard.Keep.ordinal()) {
                throw new ActionUnavailableException("Keep is indestructible.");
            }
            CitadelPlayer currentPlayer = game.getCurrentPlayer();
            int coinsLeft = currentPlayer.getCoins() - (DistrictCard.values()[districtCardId].getCost() - 1);
            if (coinsLeft < 0) {
                throw new ActionUnavailableException("Not enough coins to pay for the destruction.");
            }
            currentPlayer.setCoins(coinsLeft);
            targetPlayer.getDistricts().remove((Integer) districtCardId);
        }

        // marked ability as used
        int abilityIdx = character.getAbilities().indexOf(ability);
        game.getCharacterStatus().get(currentCharacterIdx).getAbilityUsed()[abilityIdx] = true;
        JudgeManager.afterMove(game);
        return game;
    }

    private void handleCollect(CitadelGame game, DistrictCard.DistrictCardType type) {
        CitadelPlayer player = game.getCurrentPlayer();
        int coins = player.getDistrictMap().get(type.ordinal()).size();
        player.setCoins(player.getCoins() + coins);
    }

    /**
     * @param ability           技能
     * @param stealAbilityParam 偷窃技能参数
     */
    private record UseAbilityActionBody(
            CitadelGameCharacter.Ability ability,
            AssassinateAbilityParam assassinateAbilityParam,
            StealAbilityParam stealAbilityParam,
            MagicAbilityParam magicAbilityParam,
            DestroyAbilityParam destroyAbilityParam
    ) {
        public record AssassinateAbilityParam(int characterIdx) {
        }

        public record StealAbilityParam(int characterIdx) {
        }

        /**
         * @param option      0 for exchange, 1 for discard and draw
         * @param playerId
         * @param discardList cardIds
         */
        public record MagicAbilityParam(int option, String playerId, int[] discardList) {
        }

        /*Collect不需要*/

        public record DestroyAbilityParam(String playerId, int districtCardId) {
        }


    }

}
