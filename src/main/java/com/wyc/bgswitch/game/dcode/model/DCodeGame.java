package com.wyc.bgswitch.game.dcode.model;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.game.dcode.constant.DCodeGameStatusType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class DCodeGame {
    @Setter
    private DCodeGameStatusType status; // 0准备，1进行中，2
    private final List<Integer> black = new ArrayList<>();
    private final List<Integer> white = new ArrayList<>();
    private final List<DCodePlayer> players = new ArrayList<>();
    @Setter
    private Integer currentPlayerIdx; // 当前玩家
    @Setter
    private DCodeCard pullCard; // 抽的牌
    @Setter
    private List<Integer> slotRange; // 抽牌后可以插入的位置
    @Setter
    private Boolean guessed; // 当前玩家是否已经猜过，猜过才可以end
    @Setter
    private Integer winner;

    // 前端的额外数据
    @Setter
    private Boolean isCurrentPlayer = false; // 是否当前玩家
    @Setter
    private Integer playerIdx = null; // 当前玩家的idx

    public DCodeGame() {}

    public void reset() {
        status = DCodeGameStatusType.PREPARE;
        for (int i = 0; i < 13; i++) {
            black.add(2 * i);
            white.add(2 * i + 1);
        }
        Collections.shuffle(black);
        Collections.shuffle(white);
        currentPlayerIdx = 0;
        pullCard = null;
        slotRange = null;
        guessed = false;
        winner = -1;
    }

    /**
     * methods
     */
    public DCodePlayer findPlayer(String userId) {
        for(DCodePlayer player: this.getPlayers()) {
            if(player.getUserId().equals(userId)) {
                return player;
            }
        }
        return null;
    }

    public DCodePlayer useCurrentPlayer() {
        return players.get(currentPlayerIdx);
    }

    public void attachInfo(String userId) {
        if(!players.isEmpty()) {
            for(DCodePlayer player: players) {
                if(player.getUserId().equals(userId)) {
                    this.setPlayerIdx(player.getIdx());
                    this.setIsCurrentPlayer(Objects.equals(currentPlayerIdx, player.getIdx()));
                    return;
                }
            }
        }
    }

    public DCodeGame clone() {
        return JSON.parseObject(JSON.toJSONString(this), DCodeGame.class);
    }
}
