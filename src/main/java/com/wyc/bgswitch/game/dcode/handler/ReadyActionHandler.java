package com.wyc.bgswitch.game.dcode.handler;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.common.model.weapp.UserInfo;
import com.wyc.bgswitch.game.dcode.annotation.DCodeHandler;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;
import com.wyc.bgswitch.game.dcode.model.DCodePlayer;
import com.wyc.bgswitch.game.dcode.util.ActionAssertUtil;

/**
 * @author wyc
 */
@DCodeHandler(DCodeGameActionType.READY)
public class ReadyActionHandler implements DCodeActionHandler {
    private static Integer PLAYER_NUM = 2;

    @Override
    public void check(DCodeGame game, DCodeGameAction action, String userId) {
        ActionAssertUtil.assertStatusPrepare(game);
    }

    @Override
    public DCodeGame handle(DCodeGame game, DCodeGameAction action, String userId) {
        UserInfo userInfo = JSON.parseObject(action.getBody(), UserInfo.class);
        // 创建新玩家
        if (game.getPlayers().stream().noneMatch(p -> p.getUserId().equals(userId))) {
            DCodePlayer p = new DCodePlayer(game.getPlayers().size(), userId, userInfo);
            game.getPlayers().add(p);
        }
        // 人数够了则游戏开始
        if (game.getPlayers().size() == PLAYER_NUM) {
            game.setStatus(DCodeGameStatusType.SETUP);
            game.setCurrentPlayerIdx(0);
        }
        return game;
    }
}
