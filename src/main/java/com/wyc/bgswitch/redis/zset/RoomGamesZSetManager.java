package com.wyc.bgswitch.redis.zset;

import static com.wyc.bgswitch.redis.constant.ZSetPrefix.ROOM_GAMES;

import com.wyc.bgswitch.redis.constant.ZSetPrefix;

import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * @author wyc
 * todo 其实按照目前的设计，room-game用list会更合理
 */
@Component
public class RoomGamesZSetManager extends ZSetManager<String> {
    @Resource(name = "stringRedisTemplate")
    private ZSetOperations<String, String> zSetOps;

    @Override
    ZSetOperations<String, String> getZSetOps() {
        return zSetOps;
    }

    @Override
    ZSetPrefix getPrefix() {
        return ROOM_GAMES;
    }
}
