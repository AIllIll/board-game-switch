package com.wyc.bgswitch.redis.zset;

/**
 * @author wyc
 */

public enum ZSetPrefix {
    USER_ROOMS("user-rooms"),
    ROOM_USERS("room-users"),
    ROOM_GAMES("room-games"),
    DEFAULT(null);
    private static final String CONNECTOR = ":";
    final String name;

    ZSetPrefix(String name) {
        this.name = name;
    }

    public String concat(String suffix) {
        return String.format("%s%s%s", name, CONNECTOR, suffix);
    }

}
