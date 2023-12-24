package com.wyc.bgswitch.config.lock;


/**
 * enum的顺序就是上锁顺序，必须从小到大上锁避免死锁。
 * 同一类的锁，则按后缀排序
 *
 * @author wyc
 */

public enum RedisLockPrefix {
    LOCK_PREFIX_USER("user"),
    LOCK_PREFIX_USERNAME("username"), // for register
    LOCK_PREFIX_ROOM_USERS("room-users"),
    LOCK_PREFIX_USER_ROOMS("user-rooms"),
    DEFAULT(null);

    private static final String CONNECTOR = "_";
    final String name;

    RedisLockPrefix(String name) {
        this.name = name;
    }

    public String concat(String suffix) {
        return String.format("%s%s%s", name, CONNECTOR, suffix);
    }

}
