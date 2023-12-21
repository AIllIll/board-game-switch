package com.wyc.bgswitch.redis.entity.game.citadel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wyc
 */
@Data
@AllArgsConstructor
public class Player {
    private Integer id;
    private String userId;
    private Integer score;
}
