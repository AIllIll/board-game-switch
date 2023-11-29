package com.wyc.bgswitch.game.citadel;

import com.wyc.bgswitch.game.GameEffect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class CitadelEffect extends GameEffect {
    private String gameId;
    private String effect;
    private String content;
}
