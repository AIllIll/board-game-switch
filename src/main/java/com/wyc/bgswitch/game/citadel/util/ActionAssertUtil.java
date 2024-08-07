package com.wyc.bgswitch.game.citadel.util;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.constant.GameStatus;
import com.wyc.bgswitch.game.exception.ActionUnavailableException;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.List;

/**
 * @author wyc
 */
public class ActionAssertUtil {
    /**
     * 准备阶段
     *
     * @param game
     * @return
     */
    public static void assertStatusPrepare(CitadelGame game) {
        if (!GameStatus.PREPARE.equals(game.getStatus())) {
            throw new ActionUnavailableException("The game has started");
        }
    }

    /**
     * 进行阶段
     *
     * @param game
     * @return
     */
    public static void assertStatusOngoing(CitadelGame game) {
        if (!GameStatus.ONGOING.equals(game.getStatus())) {
            throw new ActionUnavailableException("The game is not ongoing.");
        }
    }


    /**
     * 玩家是否房主
     *
     * @param game
     * @param userId
     * @return
     */
    public static void assertIsHost(CitadelGame game, String userId) {
        List<CitadelPlayer> playerList = game.getPlayers();
        if (!playerList.stream().map(CitadelPlayer::getUserId).toList().contains(userId)) {
            throw new ActionUnavailableException("Host only.");
        }
    }

    /**
     * 座位没有人
     *
     * @param game
     * @param seatIdx
     * @return
     */
    public static void assertSeatAvailable(CitadelGame game, int seatIdx) {
        List<CitadelPlayer> playerList = game.getPlayers();
        if (playerList.get(seatIdx).getUserId() != null) {
            throw new ActionUnavailableException("The seat have been taken.");
        }
    }

    /**
     * player is sitting
     *
     * @param game
     * @param playerId
     * @return
     */
    public static void assertPlayerIsSitting(CitadelGame game, String playerId) {
        if (!game.getPlayers().stream().map(CitadelPlayer::getUserId).toList().contains(playerId)) {
            throw new ActionUnavailableException("The player didn't sit.");
        }
    }


    /**
     * 玩家没满
     *
     * @param game
     * @return
     */
    public static void assertPlayerNotFull(CitadelGame game) {
        int seats = game.getPlayers().stream().filter(p -> p.getUserId() == null).toList().size();
        if (seats == 0) {
            throw new ActionUnavailableException("No available seats.");
        }
    }


    /**
     * 玩家满了
     *
     * @param game
     * @return
     */
    public static void assertPlayerFull(CitadelGame game) {
        int seats = game.getPlayers().stream().filter(p -> p.getUserId() == null).toList().size();
        if (seats != 0) {
            throw new ActionUnavailableException("Need more players.");
        }
    }


    /**
     * 轮到玩家选角色
     *
     * @param game
     */
    public static void assertCorrectTurnToPick(CitadelGame game, String userId) {
        if (!game.isInPickingTurn() || !game.getCurrentPlayer().getUserId().equals(userId)) {
            throw new ActionUnavailableException("It's not your turn to pick character.");
        }
    }

    /**
     * 角色可以被选择
     *
     * @param game
     * @param characterIdx
     */
    public static void assertCharacterAvailable(CitadelGame game, Integer characterIdx) {
        if (!game.getCharacterCardStatus().get(characterIdx).equals(CitadelGameCharacter.CardStatus.AVAILABLE)) {
            throw new ActionUnavailableException("The character has been picked.");
        }
    }

    /**
     * 轮到玩家持有角色行动
     *
     * @param game
     */
    public static void assertCorrectTurnToMove(CitadelGame game, String userId) {
        if (!game.isInCharacterTurn()) {
            throw new ActionUnavailableException("It's not your turn to move.");
        }
        CitadelGameCharacter character = CitadelGameCharacter.values()[game.getCurrentCharacterIdx()];
        CitadelPlayer player = game.getPlayers().stream().filter(p -> p.getUserId().equals(userId)).findAny()
                .orElse(CitadelPlayer.emptyPlayer());
        if (!player.getCharacters().contains(character)) {
            throw new ActionUnavailableException("It's not your turn to move.");
        }
    }


    /**
     * 额外回合
     *
     * @param game
     */
    public static void assertInExtraTurn(CitadelGame game) {
        if (!game.isInExtraTurn()) {
            throw new ActionUnavailableException("It's not in extra turns.");
        }
    }

    /**
     * 玩家尚未获取钱/卡
     *
     * @param game
     */
    public static void assertPlayerNotCollected(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (player.getStatus().isCollected()) {
            throw new ActionUnavailableException("Player has already collected.");
        }
    }

    /**
     * 玩家不在选卡
     *
     * @param game
     */
    public static void assertPlayerNotCollecting(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (player.getStatus().isCollecting()) {
            throw new ActionUnavailableException("Player is collecting.");
        }
    }

    /**
     * 玩家正在选卡
     *
     * @param game
     */
    public static void assertPlayerCollecting(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (!player.getStatus().isCollecting()) {
            throw new ActionUnavailableException("Player is not collecting.");
        }
    }

    /**
     * 玩家已经获取钱/卡
     *
     * @param game
     */
    public static void assertPlayerCollected(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (!player.getStatus().isCollected()) {
            throw new ActionUnavailableException("Player has not collected yet.");
        }
    }

    /**
     * 角色状态正确
     * 1. 如果是刺客或者小偷，必须使用技能
     *
     * @param game
     */
    public static void assertCharacterStatusCorrectedBeforeEndTurn(CitadelGame game) {
        CitadelGameCharacter.InGameStatus status = game.getCharacterStatus().get(game.getCurrentCharacterIdx());
        if (game.getCurrentCharacterIdx() == CitadelGameCharacter.ASSASSIN.ordinal() && !status.getAbilityUsed()[0]) {
            throw new ActionUnavailableException("Player has not announced target to assassinate from yet.");
        }
        if (game.getCurrentCharacterIdx() == CitadelGameCharacter.THIEF.ordinal() && !status.getAbilityUsed()[0]) {
            // 如果小偷被刺杀，会由系统跳过回合，不需要手动结束
            throw new ActionUnavailableException("Player has not announced target to stolen from yet.");
        }
    }

    /**
     * 用户之前抽了卡
     *
     * @param game
     */
    public static void assertDrawnCardNotEmpty(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (player.getDrawnCards() == null || player.getDrawnCards().size() == 0) {
            throw new ActionUnavailableException("Drawn district cards not found.");
        }
    }

    /**
     * 保留的卡的数量正确
     *
     * @param game
     */
    public static void assertKeepingCardNumber(CitadelGame game, CitadelGameAction action) {
        List<Integer> keptCards = JSON.parseArray(action.getBody(), Integer.class);
        CitadelPlayer player = game.getCurrentPlayer();
        if (keptCards.size() > player.getStatus().getKeep()) {
            throw new ActionUnavailableException("Keep too many district cards.");
        }
    }

    /**
     * 可以建
     */
    public static void assertCanBuild(CitadelGame game, CitadelGameAction action) {
        CitadelPlayer player = game.getCurrentPlayer();
        if (player.getStatus().getBuildTimes() <= 0) {
            throw new ActionUnavailableException("You have no more build times.");
        }

        int cardId = JSON.parseObject(action.getBody(), Integer.class);
        // 卡不存在
        if (cardId >= DistrictCard.values().length) {
            throw new ActionUnavailableException("District card not found.");
        }
        List<Integer> districts = player.getDistricts();
        // 重复建造
        if (districts != null &&
                districts.contains(cardId)) {
            throw new ActionUnavailableException("An identical district has been built.");
        }
        // 钱够
        DistrictCard card = DistrictCard.values()[cardId];
        if (player.getCoins() < card.getCost()) {
            throw new ActionUnavailableException("You don't have enough coins.");
        }
    }

    /**
     * 可以使用技能
     *
     * @param game
     * @param ability 技能
     */
    public static void assertCanUseAbility(CitadelGame game, CitadelGameCharacter.Ability ability) {
        int currentCharacterIdx = game.getCurrentCharacterIdx();
        CitadelGameCharacter.InGameStatus status = game.getCharacterStatus().get(currentCharacterIdx);
        CitadelGameCharacter character = CitadelGameCharacter.values()[currentCharacterIdx];
        // not killed
        if (status.isAssassinated()) {
            throw new ActionUnavailableException("Your character have been assassinated.");
        }
        // character has that ability
        if (!character.getAbilities().contains(ability)) {
            throw new ActionUnavailableException("Your current character doesn't have that ability.");
        }
        // it hasn't been used
        int abilityIdx = character.getAbilities().indexOf(ability);
        if (status.getAbilityUsed()[abilityIdx]) {
            throw new ActionUnavailableException("You have used the ability.");
        }
    }

    /**
     * 可以使用地区
     *
     * @param game
     * @param district 地区
     */
    public static void assertCanUseDistrict(CitadelGame game, DistrictCard district) {
        int currentCharacterIdx = game.getCurrentCharacterIdx();
        CitadelGameCharacter.InGameStatus status = game.getCharacterStatus().get(currentCharacterIdx);
        // not killed
        if (status.isAssassinated()) {
            throw new ActionUnavailableException("Your character have been assassinated.");
        }
        // player have built that district
        if (!game.getCurrentPlayer().getDistricts().contains(district.ordinal())) {
            throw new ActionUnavailableException("The district is not yours to use.");
        }
        // player haven't used that district
        if (game.getCurrentPlayer().getStatus().getUsedDistricts().contains(district)) {
            throw new ActionUnavailableException("The district has been used.");
        }
    }

    /**
     * 可以使用Graveyard
     */
    public static void assertCanUseGraveyard(CitadelGame game) {
        if (!game.isInExtraTurnOfGraveyard()) {
            throw new ActionUnavailableException("Can't use Graveyard now.");
        }
        // player have built that district
        if (!game.getCurrentPlayer().getDistricts().contains(DistrictCard.Graveyard.ordinal())) {
            throw new ActionUnavailableException("The district is not yours to use.");
        }
        // enough coins
        if (game.getCurrentPlayer().getCoins() < 1) {
            throw new ActionUnavailableException("You don't have enough coins to use Graveyard.");
        }
        // player haven't used that district
        if (game.getCurrentPlayer().getStatus().getUsedDistricts().contains(DistrictCard.Graveyard)) {
            throw new ActionUnavailableException("The district has been used.");
        }
    }
}
