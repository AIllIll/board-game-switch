package com.wyc.bgswitch.game.citadel.handler;

import com.alibaba.fastjson2.JSONObject;
import com.wyc.bgswitch.game.annotation.Handler;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.util.ActionAssertUtil;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
@Handler(CitadelGameActionType.CONFIG)
public class ConfigActionHandler implements ActionHandler {
    @Override
    public void check(CitadelGame game, CitadelGameAction action, String userId) {
        ActionAssertUtil.assertStatusPrepare(game);
        ActionAssertUtil.assertIsHost(game, userId);
    }

    @Override
    public CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId) {
        CitadelGameConfig config = JSONObject.parseObject(action.getBody(), CitadelGameConfig.class);
        game.setConfig(config);
        return game;
    }
}
