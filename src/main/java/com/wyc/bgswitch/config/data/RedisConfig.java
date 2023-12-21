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

    @Bean
    public RedisScript<Boolean> casScript() {
        Resource scriptSource = new ClassPathResource("redis/check-and-set.lua");
        return RedisScript.of(scriptSource, Boolean.class);
    }
}
