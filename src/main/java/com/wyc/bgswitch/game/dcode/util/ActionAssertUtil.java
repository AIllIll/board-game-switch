package com.wyc.bgswitch.game.dcode.util;

import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.exception.ActionUnavailableException;

/**
 * @author wyc
 */
public class ActionAssertUtil {
    /**
     * 准备阶段
     *
     * @param game game
     */
    public static void assertStatusPrepare(DCodeGame game) {
        if (!DCodeGameStatusType.PREPARE.equals(game.getStatus())) {
            throw new ActionUnavailableException("You can't join the game now.");
        }
    }

    /**
     * 猜测阶段
     *
     * @param game game
     */
    public static void assertStatusOnGoing(DCodeGame game) {
        if (!DCodeGameStatusType.ONGOING.equals(game.getStatus())) {
            throw new ActionUnavailableException("Guess can't be on the game now.");
        }
    }

    /**
     * 是我的回合
     */
    public static void assertIsMyTurn(DCodeGame game, String userId) {
        if(!game.useCurrentPlayer().getUserId().equals(userId)) {
            throw new ActionUnavailableException("It's not your turn.");
        }
    }

    /**
     * 已经抽牌
     */
    public static void assertPulled(DCodeGame game) {
        if(game.getPullCard() == null) {
            throw new com.wyc.bgswitch.game.dcode.exception.ActionUnavailableException("You must pull a card first.");
        }
    }

}
