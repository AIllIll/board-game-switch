package com.wyc.bgswitch.game.citadel.util;

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
     * 准备阶段可以改
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
}
