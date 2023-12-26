package com.wyc.bgswitch.redis.entity;


import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@RedisHash("bgs/repo/auth")
@Data
public class Auth {
    @Id
    private String id;
    @Indexed
    @NonNull
    private String username;
    @Required
    @NonNull
    private String password;
    @NonNull
    private List<ROLES> roles;

    public enum ROLES {
        USER("USER"),
        ADMIN("ADMIN");
        public static final String PREFIX = "ROLE_";
        public final String name;

        ROLES(String name) {
            this.name = name;
        }
    }
}