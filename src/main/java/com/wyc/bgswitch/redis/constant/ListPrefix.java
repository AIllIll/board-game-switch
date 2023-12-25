package com.wyc.bgswitch.redis.constant;

/**
 * @author wyc
 */

public enum ListPrefix {
    CITADEL_ACTIONS("citadel-actions"),
    DEFAULT(null);
    private static final String CONNECTOR = ":";
    final String name;

    ListPrefix(String name) {
        this.name = name;
    }

    public String concat(String suffix) {
        return String.format("%s%s%s", name, CONNECTOR, suffix);
    }

}
