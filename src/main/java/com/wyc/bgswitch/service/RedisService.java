package com.wyc.bgswitch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

/**
 * @author wyc
 */
@Service
public class RedisService {

    // inject the actual operations
    private final RedisOperations<String, Object> operations;
    private final RedisKeyValueTemplate keyValueTemplate;
    private final RedisScript<Boolean> hashCasScript;
    // inject the template as ListOperations
    @Resource(name = "myRedisTemplate")
    private ListOperations<String, Object> listOps;
    @Resource(name = "myRedisTemplate")
    private HashOperations<String, String, Object> hashOps;

    @Autowired
    public RedisService(@Qualifier("myRedisTemplate") RedisOperations<String, Object> operations, RedisKeyValueTemplate keyValueTemplate, RedisScript<Boolean> hashCasScript) {
        this.operations = operations;
        this.keyValueTemplate = keyValueTemplate;
        this.hashCasScript = hashCasScript;
    }

    public void addToListLeft(String redisKey, String item) {
        listOps.leftPush(redisKey, item);
        System.out.println("size" + listOps.size(redisKey));
        System.out.println("range" + listOps.range(redisKey, Long.MIN_VALUE, Long.MAX_VALUE));
    }

    public void addToHash(String redisKey, String key, Object o) {
        hashOps.put(redisKey, key, o);
    }

    public void setValue(String redisKey, Object value) {
        operations.opsForValue().set(redisKey, value);
    }

    /**
     * @param redisKey          redis key
     * @param versionKey        key of version field
     * @param localVersion      version value of local copy
     * @param newVersion        version value of new copy
     * @param updateFieldKeys   keys of fields to update
     * @param updateFieldValues values of fields to update
     * @return success or not
     */
    public Boolean atomicHashCAS(String redisKey, String versionKey, String localVersion, String newVersion, List<String> updateFieldKeys, List<String> updateFieldValues) {
        List<String> keys = new ArrayList<>();
        keys.add(redisKey);
        keys.add(versionKey);
        keys.addAll(updateFieldKeys);
        List<String> argv = new ArrayList<>();
        argv.add(localVersion);
        argv.add(newVersion);
        argv.addAll(updateFieldValues);
        return operations.execute(hashCasScript, keys, argv);
    }
}
