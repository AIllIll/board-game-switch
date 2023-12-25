package com.wyc.bgswitch.redis.zset;

import com.wyc.bgswitch.redis.constant.ZSetPrefix;

import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author wyc
 */
public abstract class ZSetManager<V> {
    abstract ZSetOperations<String, V> getZSetOps();

    abstract ZSetPrefix getPrefix();

    private String key(String k) {
        return getPrefix().concat(k);
    }

    /**
     * get all
     *
     * @param zSetKey
     * @return
     */
    public List<V> getAll(String zSetKey) {
        return Optional.ofNullable(getZSetOps().range(key(zSetKey), 0, Long.MAX_VALUE))
                .orElse(Collections.emptySet())
                .stream().toList();
    }

    public V getLast(String zSetKey) {
        List<V> l = Optional.ofNullable(getZSetOps().range(key(zSetKey), -1, -1))
                .orElse(Collections.singleton(null)).stream().toList();
        return l.size() > 0 ? l.get(0) : null;
    }

    public V getFirst(String zSetKey) {
        List<V> l = Optional.ofNullable(getZSetOps().range(key(zSetKey), 0, 0))
                .orElse(Collections.singleton(null)).stream().toList();
        return l.size() > 0 ? l.get(0) : null;
    }


    public Boolean add(String zSetKey, V v) {
        // 用时间作为score，使按加入set的时间顺序排序
        return getZSetOps().addIfAbsent(key(zSetKey), v, new Date().getTime());
    }

    public Boolean remove(String zSetKey, V v) {
        Long n = getZSetOps().remove(key(zSetKey), v);
        if (n == null) {
            n = 0L;
        }
        return n > 0;
    }

    public Boolean in(String zSetKey, V v) {
        return getAll(key(zSetKey)).contains(v);
    }
}
