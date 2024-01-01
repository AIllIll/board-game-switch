package com.wyc.bgswitch.game.citadel.model;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@Data
public class CitadelGameConfig {
    @NonNull
    private Integer playerNumber;

    public static CitadelGameConfig getDefaultConfig() {
        return new CitadelGameConfig(4);
    }
}
