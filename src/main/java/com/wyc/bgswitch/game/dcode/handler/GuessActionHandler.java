package com.wyc.bgswitch.game.dcode.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.dcode.annotation.DCodeHandler;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import com.wyc.bgswitch.game.dcode.exception.ActionUnavailableException;
import com.wyc.bgswitch.game.dcode.model.DCodeCard;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.game.dcode.model.DCodePlayer;
import com.wyc.bgswitch.game.dcode.util.ActionAssertUtil;


/**
 * @author wyc
 */
@DCodeHandler(DCodeGameActionType.GUESS)
public class GuessActionHandler implements DCodeActionHandler {

    @Override
    public void check(DCodeGame game, DCodeGameAction action, String userId) {
        ActionAssertUtil.assertStatusOnGoing(game);
        ActionAssertUtil.assertIsMyTurn(game, userId);
        ActionAssertUtil.assertPulled(game);
        this.assertPullCardNotFallen(game);
    }

    @Override
    public DCodeGame handle(DCodeGame game, DCodeGameAction action, String userId) {
        GuessBody body = JSON.parseObject(action.getBody(), GuessBody.class);
        DCodePlayer player = game.getPlayers().get(body.playerIdx);
        // 判断猜测
        DCodeCard card = player.getHand().get(body.handIdx);
        if (card.getLabel().equals(body.label)) {
            card.setFall(true);
        } else {
            // 猜错了，自己新牌倒下
            game.getPullCard().setFall(true);
        }
        // 手牌全倒，游戏结束
        if (player.getHand().stream().allMatch(DCodeCard::getFall)) {
            game.setWinner(1 - body.playerIdx);
            game.setStatus(DCodeGameStatusType.END);
        }
        // 标记已经guess
        game.setGuessed(true);
        return game;
    }

    private record GuessBody(Integer playerIdx, Integer handIdx, String label) {
    }

    /**
     * 没有猜牌失败
     */
    public void assertPullCardNotFallen(DCodeGame game) {
        if (game.getPullCard().getFall()) {
            throw new ActionUnavailableException("You've failed guessing.");
        }
    }

}
