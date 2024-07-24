package com.wyc.bgswitch.game.dcode.constant;


/**
 * @author wyc
 */
public enum DCodeGameActionType {
    READY("READY"),
    PULL("PULL"),
    INSERT("INSERT"),
    GUESS("GUESS"),
    END("END"),
    ;
    private final String value;

    DCodeGameActionType(String value) {
        this.value = value;
    }
}
