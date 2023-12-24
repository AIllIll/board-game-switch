package com.wyc.bgswitch.redis.repository;

import com.wyc.bgswitch.redis.BaseRepository;
import com.wyc.bgswitch.redis.entity.Auth;

import java.util.List;

/**
 * @author wyc
 */
public interface AuthRepository extends BaseRepository<Auth, String> {
    List<Auth> findByUsername(String username);
}
