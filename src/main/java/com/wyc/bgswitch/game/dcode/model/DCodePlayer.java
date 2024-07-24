package com.wyc.bgswitch.game.dcode.model;

import com.wyc.bgswitch.common.model.weapp.UserInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class DCodePlayer {
    private final Integer idx;
    private final String userId;
    private final List<DCodeCard> hand;

    private final UserInfo userInfo;

    public DCodePlayer(Integer idx, String userId, UserInfo userInfo) {
        this.idx = idx;
        this.userId = userId;
        this.userInfo = userInfo;
        this.hand = new ArrayList<>();
    }
}