package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
public interface ActionHandler {
    void check(CitadelGame game, CitadelGameAction action, String userId);

    CitadelGame handle(CitadelGame game, CitadelGameAction action, String userId);
}
