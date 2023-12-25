package com.wyc.bgswitch.game.citadel.model;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameActionType;

import lombok.Data;

/**
 * @author wyc
 */
@Data
public class CitadelGameAction {
    private CitadelGameActionType type;
    private String body;
}
