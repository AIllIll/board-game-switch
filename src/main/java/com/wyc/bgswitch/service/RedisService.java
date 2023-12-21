package com.wyc.bgswitch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * @author wyc
 */
@Service
public class RedisService {

    // inject the actual operations
    private final RedisOperations<String, Object> operations;
    // inject the template as ListOperations
    @Resource(name = "myRedisTemplate")
    private ListOperations<String, Object> listOps;
    @Resource(name = "myRedisTemplate")
    private HashOperations<String, String, Object> hashOps;

    @Autowired
    public RedisService(@Qualifier("myRedisTemplate") RedisOperations<String, Object> operations) {
        this.operations = operations;
    }

    public void addToList(String redisKey, String item) {
        listOps.leftPush(redisKey, item);
        System.out.println("size" + listOps.size(redisKey));
        System.out.println("range" + listOps.range(redisKey, Long.MIN_VALUE, Long.MAX_VALUE));
    }

    public void addToHash(String redisKey, String key, Object o) {
        hashOps.put(redisKey, key, o);
    }

    public void addValue(String redisKey, Object value) {
        operations.opsForValue().set(redisKey, value);
    }

//    public List<String> getList(String key) {
//    }
}
