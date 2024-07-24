package com.wyc.bgswitch.game.dcode.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 卡牌
 */
@Getter
public class DCodeCard {
    @Setter
    private Boolean fall;
    private final Integer number;
    private final String color;
    private final String label;

    public DCodeCard(Integer number) {
        this.number = number;
        this.color = number % 2 == 0 ? "black" : "white";
        if(number >= 24) {
            this.label = "-";
        } else {
            this.label = "%d".formatted(number / 2);
        }
        this.fall = false;
    }
}
