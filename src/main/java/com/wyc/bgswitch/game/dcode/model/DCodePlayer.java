package com.wyc.bgswitch.game.dcode.model;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DCodePlayer {
    private final Integer idx;
    private final String userId;
    private final List<DCodeCard> hand;

    public DCodePlayer(Integer idx, String userId) {
        this.idx = idx;
        this.userId = userId;
        this.hand = new ArrayList<>();
    }
}