package com.wyc.bgswitch.config.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @author wyc
 */
@Configuration
public class RedisLockConfig {
    @Bean(destroyMethod = "destroy")
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        return new RedisLockRegistry(
                connectionFactory,
                "bgs/redis-lock",
                10000L // 默认是60000L即60s
        );
    }
}
