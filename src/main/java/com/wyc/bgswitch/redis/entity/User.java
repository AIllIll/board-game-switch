package com.wyc.bgswitch.redis.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@RedisHash("bgs/repo/user")
@Data
@AllArgsConstructor
public class User {
    @Id
    private String id;
    private String name;
    private String avatar;
}
