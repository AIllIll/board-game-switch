package com.wyc.bgswitch.game.citadel.model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@Data
public class CitadelGameConfig {
    public static CitadelGameConfig defaultConfig = new CitadelGameConfig(4);
    @NonNull
    private Integer playerNumber;
    private Long createdAt;
}
