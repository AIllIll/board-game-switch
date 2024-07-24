package com.wyc.bgswitch.game.dcode.handler;

import com.wyc.bgswitch.game.dcode.model.DCodeGame;
import com.wyc.bgswitch.game.dcode.model.DCodeGameAction;

/**
 * @author wyc
 */
public interface DCodeActionHandler {
    void check(DCodeGame game, DCodeGameAction action, String userId);

    DCodeGame handle(DCodeGame game, DCodeGameAction action, String userId);
}
