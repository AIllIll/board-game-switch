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
     * 座位有人
     *
     * @param game
     * @param seatIdx
     * @return
     */
    public static void assertSeatTaken(CitadelGame game, int seatIdx) {
        List<CitadelPlayer> playerList = game.getPlayers();
        if (playerList.get(seatIdx).getUserId() == null) {
            throw new ActionUnavailableException("The seat have been taken.");
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
        if (game.getTurn() < 0) {
            throw new ActionUnavailableException("It's not your turn to pick character.");
        }
        int currentPlayerIdx = (game.getCrown() + game.getTurn()) % game.getPlayers().size();
        if (!game.getPlayers().get(currentPlayerIdx).getUserId().equals(userId)) {
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
        if (game.getTurn() < 2 * game.getPlayers().size()) {
            throw new ActionUnavailableException("It's not your turn to pick character.");
        }
        int currentCharacterIdx = game.getTurn() - 2 * game.getPlayers().size();
        CitadelGameCharacter character = CitadelGameCharacter.values()[currentCharacterIdx];
        CitadelPlayer player = game.getPlayers().stream().filter(p -> p.getUserId().equals(userId)).findAny()
                .orElse(CitadelPlayer.emptyPlayer());
        if (!player.getCharacters().contains(character)) {
            throw new ActionUnavailableException("It's not your turn to move.");
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
     * 玩家尚未获取钱/卡
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
     * 用户之前抽了卡
     *
     * @param game
     */
    public static void assertDrawnCardNotEmpty(CitadelGame game) {
        CitadelPlayer player = game.getCurrentPlayer();
        // todo
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
        // todo
        if (keptCards.size() != 1) {
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

        int handIdx = JSON.parseObject(action.getBody(), Integer.class); // 第几张手牌
        // 手牌不够
        if (game.getCurrentPlayer().getHand() == null || game.getCurrentPlayer().getHand().size() < handIdx + 1) {
            throw new ActionUnavailableException("District card not found.");
        }
        Integer cardIdx = game.getCurrentPlayer().getHand().get(handIdx);
        // 卡不存在
        if (cardIdx >= DistrictCard.values().length) {
            throw new ActionUnavailableException("District card not found.");
        }
        List<Integer> districts = player.getDistricts();
        // 重复建造
        if (districts != null &&
                districts.contains(cardIdx)) {
            throw new ActionUnavailableException("An identical district has been built.");
        }
        // 钱够
        DistrictCard card = DistrictCard.values()[cardIdx];
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
}
