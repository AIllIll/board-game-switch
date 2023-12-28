package com.wyc.bgswitch.game.citadel.util;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
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
     * @param game
     */
    public static void assertCorrectTurnToPick(CitadelGame game, String userId) {
        if (game.getPickingTurn() < 0) {
            throw new ActionUnavailableException("It's not your turn to pick character.");
        }
        int currentPlayerIdx = (game.getCrown() + game.getPickingTurn()) % game.getPlayers().size();
        if (!game.getPlayers().get(currentPlayerIdx).getUserId().equals(userId)) {
            throw new ActionUnavailableException("It's not your turn to pick character.");
        }
    }

    public static void assertCharacterAvailable(CitadelGame game, Integer characterIdx) {
        if (!game.getCharacterCardStatus().get(characterIdx).equals(CitadelGameCharacter.CardStatus.AVAILABLE)) {
            throw new ActionUnavailableException("The character has been picked.");
        }
    }
}
