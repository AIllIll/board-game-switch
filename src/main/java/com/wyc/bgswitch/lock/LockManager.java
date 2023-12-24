package com.wyc.bgswitch.lock;

import com.wyc.bgswitch.config.lock.RedisLockPrefix;

import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
@Component
public class LockManager {
    private final RedisLockRegistry redisLockRegistry;


    public LockManager(RedisLockRegistry redisLockRegistry) {
        this.redisLockRegistry = redisLockRegistry;
    }

    public MultiLockBuilder useBuilder() {
        return new MultiLockBuilder();
    }

    /**
     * @author wyc
     */
    public class MultiLockBuilder {
        private final Map<RedisLockPrefix, Set<String>> map = new HashMap<>();
        private RedisLockPrefix current;

        public MultiLockBuilder obtain(RedisLockPrefix prefix) {
            current = prefix;
            map.putIfAbsent(prefix, new HashSet<>());
            return this;
        }

        public MultiLockBuilder of(String target) {
            map.get(current).add(target);
            return this;
        }

        public MultiLock build() {
            List<RedisLockPrefix> keys = new ArrayList<>(map.keySet());
            keys.sort(RedisLockPrefix::compareTo);
            List<String> l = new ArrayList<>();
            keys.forEach(k -> {
                List<String> suffixes = new ArrayList<>(map.get(k));
                suffixes.sort(String::compareTo);
                suffixes.forEach(s -> l.add(k.concat(s)));
            });
            return new MultiLock(l.stream().map(redisLockRegistry::obtain).collect(Collectors.toList()));
        }

        public static class MultiLock {
            private final List<Lock> locks;

            private MultiLock(List<Lock> locks) {
                this.locks = locks;
            }

            public void lock() {
                locks.forEach(Lock::lock);
            }

            public void unLock() {
                Collections.reverse(locks);
                locks.forEach(Lock::unlock);
            }


        }

    }
}
