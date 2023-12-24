package com.wyc.bgswitch.redis.zset;

import static com.wyc.bgswitch.redis.zset.ZSetPrefix.ROOM_USERS;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @author wyc
 */
@Component
public class RoomUsersZSetManager extends ZSetManager<String> {

    @Resource(name = "stringRedisTemplate")
    private ZSetOperations<String, String> zSetOps;

    @Override
    ZSetOperations<String, String> getZSetOps() {
        return zSetOps;
    }

    @Override
    ZSetPrefix getPrefix() {
        return ROOM_USERS;
    }

}
