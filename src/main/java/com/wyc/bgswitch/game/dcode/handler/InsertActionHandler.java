package com.wyc.bgswitch.game.dcode.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.dcode.annotation.DCodeHandler;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import com.wyc.bgswitch.game.dcode.exception.ActionUnavailableException;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.game.dcode.util.ActionAssertUtil;


/**
 * @author wyc
 */
@DCodeHandler(DCodeGameActionType.INSERT)
public class InsertActionHandler implements DCodeActionHandler {

    @Override
    public void check(DCodeGame game, DCodeGameAction action, String userId) {
        if (!DCodeGameStatusType.SETUP.equals(game.getStatus()) &&
                !DCodeGameStatusType.ONGOING.equals(game.getStatus())) {
            throw new ActionUnavailableException("You can't insert now");
        }
        ActionAssertUtil.assertIsMyTurn(game, userId);
        ActionAssertUtil.assertPulled(game);
        // 如果在ONGOING必须已经猜测过了
        if (DCodeGameStatusType.ONGOING.equals(game.getStatus())) {
            if (!game.getGuessed()) {
                throw new ActionUnavailableException("You must guess before inserting");
            }
        }
        Integer insertIdx = JSON.parseObject(action.getBody(), Integer.class);
        this.assertInCorrectSlot(game, insertIdx);
    }

    @Override
    public DCodeGame handle(DCodeGame game, DCodeGameAction action, String userId) {
        Integer insertIdx = JSON.parseObject(action.getBody(), Integer.class);
        game.useCurrentPlayer().getHand().add(insertIdx, game.getPullCard());
        this.nextTurn(game);
        return game;
    }

    /**
     * 在正确的空隙中
     */
    public void assertInCorrectSlot(DCodeGame game, Integer insertIdx) {
        if (game.getSlotRange() == null ||
                insertIdx < game.getSlotRange().get(0) ||
                game.getSlotRange().get(1) < insertIdx
        ) {
            throw new ActionUnavailableException("You can't insert here.");
        }
    }

    /**
     * 回合结束
     */
    public void nextTurn(DCodeGame game) {
        // 下一个玩家
        game.setCurrentPlayerIdx((game.getCurrentPlayerIdx() + 1) % game.getPlayers().size());
        // 重置回合状态
        game.setGuessed(false);
        game.setPullCard(null);
        game.setSlotRange(null);
        // setup到ongoing
        if (game.getStatus().equals(DCodeGameStatusType.SETUP) &&
                game.getPlayers().stream().allMatch(p -> p.getHand().size() == 4)) {
            game.setStatus(DCodeGameStatusType.ONGOING);
        }
    }
}
