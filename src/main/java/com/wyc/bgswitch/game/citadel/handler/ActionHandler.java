package com.wyc.bgswitch.game.citadel.handler;

import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.redis.entity.User;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

/**
 * @author wyc
 */
public interface ActionHandler {
    Boolean check(CitadelGame game, CitadelGameAction action, User user);

    CitadelGame handle(CitadelGame game, CitadelGameAction action, User user);
}
