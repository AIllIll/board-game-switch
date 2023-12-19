package com.wyc.bgswitch.game;

import com.wyc.bgswitch.game.base.enums.GameActionType;

public record GameAction(
        String gameId,
        GameActionType type,
        Long timestamp
) {

}
