package com.wyc.bgswitch.redis.zset;

import static com.wyc.bgswitch.redis.zset.ZSetPrefix.USER_ROOMS;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @author wyc
 */
@Component
public class UserRoomsZSetManager extends ZSetManager<String> {

    @Resource(name = "stringRedisTemplate")
    private ZSetOperations<String, String> zSetOps;


    @Override
    ZSetOperations<String, String> getZSetOps() {
        return zSetOps;
    }

    @Override
    ZSetPrefix getPrefix() {
        return USER_ROOMS;
    }

}
