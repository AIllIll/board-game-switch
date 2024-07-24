package com.wyc.bgswitch.game.dcode.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.dcode.annotation.DCodeHandler;
import com.wyc.bgswitch.game.dcode.constant.DCodeCardColor;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import com.wyc.bgswitch.game.dcode.exception.ActionUnavailableException;
import com.wyc.bgswitch.game.dcode.model.DCodeCard;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.game.dcode.model.DCodePlayer;
import com.wyc.bgswitch.game.dcode.util.ActionAssertUtil;

import java.util.List;

/**
 * @author wyc
 */
@DCodeHandler(DCodeGameActionType.PULL)
public class PullActionHandler implements DCodeActionHandler {
    @Override
    public void check(DCodeGame game, DCodeGameAction action, String userId) {
        // 游戏进程
        if (DCodeGameStatusType.SETUP.equals(game.getStatus())) {
            // 是否拿够了4张初始手牌
            if (game.findPlayer(userId).getHand().size() >= 4) {
                throw new ActionUnavailableException("You've got enough card in hand.");
            }
            // 是否已经抽卡
            if (game.getPullCard() != null) {
                throw new ActionUnavailableException("You've pulled.");
            }
        } else if (DCodeGameStatusType.ONGOING.equals(game.getStatus())) {
            // 是否已经抽卡
            if (game.getPullCard() != null) {
                throw new ActionUnavailableException("You've pulled.");
            }
        } else {
            throw new ActionUnavailableException("You can't pull now.");
        }

        // 我的回合
        ActionAssertUtil.assertIsMyTurn(game, userId);


        // 是否还有牌
        DCodeCardColor color = DCodeCardColor.valueOf(action.getBody());
        if (DCodeCardColor.BLACK.equals(color) && game.getBlack().isEmpty() ||
                DCodeCardColor.WHITE.equals(color) && game.getWhite().isEmpty()) {
            throw new ActionUnavailableException("You can't pull from an empty deck.");
        }
    }

    @Override
    public DCodeGame handle(DCodeGame game, DCodeGameAction action, String userId) {
        // 抽取卡
        DCodeCardColor color = DCodeCardColor.valueOf(action.getBody());
        Integer number;
        if (DCodeCardColor.BLACK.equals(color)) {
            number = game.getBlack().remove(0);
        } else {
            number = game.getWhite().remove(0);
        }
        game.setPullCard(new DCodeCard(number));

        // 更新slotRange
        DCodePlayer player = game.getPlayers().get(game.getCurrentPlayerIdx());
        List<DCodeCard> hand = player.getHand();
        int left = 0;
        int right = hand.size();
        if (number >= 24) {
        } else {
            for (int i = 0; i < hand.size(); i++) {
                if (hand.get(i).getLabel().equals("-")) {
                    continue;
                } else if (number > hand.get(i).getNumber()) {
                    left = i + 1;
                }
            }
            // 右侧最后一个比当前数字大的元素的idx
            for (int i = hand.size() - 1; i >= 0; i--) {
                if (hand.get(i).getNumber() >= 24) {
                    continue;
                } else if (number < hand.get(i).getNumber()) {
                    right = i;
                }
            }
        }
        game.setSlotRange(List.of(left, right));
        return game;
    }
}
