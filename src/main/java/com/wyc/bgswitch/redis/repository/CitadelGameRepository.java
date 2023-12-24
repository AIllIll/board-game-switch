package com.wyc.bgswitch.redis.repository;

import com.wyc.bgswitch.redis.BaseRepository;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.List;

/**
 * @author wyc
 */
public interface CitadelGameRepository extends BaseRepository<CitadelGame, String> {
    List<CitadelGame> findByRoomId(String roomId);
}
