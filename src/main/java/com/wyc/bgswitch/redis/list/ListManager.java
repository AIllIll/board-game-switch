package com.wyc.bgswitch.redis.list;

import com.wyc.bgswitch.redis.constant.ListPrefix;

import org.springframework.data.redis.core.ListOperations;

/**
 * @author wyc
 */
public abstract class ListManager<V> {
    abstract ListOperations<String, V> getListOps();

    abstract ListPrefix getPrefix();

    private String key(String k) {
        return getPrefix().concat(k);
    }
}
