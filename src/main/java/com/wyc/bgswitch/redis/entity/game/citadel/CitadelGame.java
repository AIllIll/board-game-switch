package com.wyc.bgswitch.redis.entity.game.citadel;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@RedisHash("/bgs/game/citadel")
@Data
@AllArgsConstructor
public class CitadelGame {
    @Id
    private String id;
    private List<Player> players;
}
