package com.wyc.bgswitch.game.dcode.model;

import com.wyc.bgswitch.game.dcode.constant.DCodeGameActionType;
import lombok.Data;

/**
 * @author wyc
 */
@Data
public class DCodeGameAction {
    private String playerId = null; // 发出action的用户名
    private DCodeGameActionType type;
    private String body;
}