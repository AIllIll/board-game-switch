package com.wyc.bgswitch.config.data;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.time.Duration;

/**
 * @author wyc
 */
@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory connectionFactory() {

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .useSsl().and()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 16379), clientConfig);
    }

    @Bean(destroyMethod = "destroy")
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory connectionFactory) {
        return new RedisLockRegistry(
                connectionFactory,
                "redis-lock",
                10000L // 默认是60000L即60s
        );
    }

    @Bean
    RedisTemplate<?, ?> myRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashValueSerializer(StringRedisSerializer.UTF_8);
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    /**
     * key-value的cas
     *
     * @return
     */
    @Bean
    public RedisScript<Boolean> simpleCasScript() {
        Resource scriptSource = new ClassPathResource("redis/check-and-set.lua");
        return RedisScript.of(scriptSource, Boolean.class);
    }

    /**
     * hash的cas
     * KEYS: [redisKey, versionFieldKey, fieldKey1, fieldKey2...]
     * 第3个KEYS开始是实际要set的field
     * ARGV: [currentVersion, newVersion, fieldValue1, fieldValue2...]
     * 第2个ARGV是新的version值，第3个开始是实际要set的field
     *
     * @return
     */
    @Bean
    public RedisScript<Boolean> hashCasScript() {
        Resource scriptSource = new ClassPathResource("redis/cas-hash.lua");
        return RedisScript.of(scriptSource, Boolean.class);
    }
}
