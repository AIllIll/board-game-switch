package com.wyc.bgswitch.game.citadel;

import com.wyc.bgswitch.game.GameAction;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CitadelAction extends GameAction {
    private String gameId;
    private String action;
    private String content;
}
