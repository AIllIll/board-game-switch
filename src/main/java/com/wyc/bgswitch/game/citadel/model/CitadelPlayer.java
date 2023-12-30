package com.wyc.bgswitch.game.citadel.model;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wyc
 */
@Data
public class CitadelPlayer {
    private String userId;
    private Integer score = 0; // 总得分
    private Integer visibleScore = 0; // 可见得分
    private CitadelGameCharacter character1; // 身份1
    private CitadelGameCharacter character2; // 身份2
    private Integer coins = 0; // 金币
    private List<Integer> hand; // 手牌
    private List<Integer> drawnCards; // 抽的牌
    private Map<Integer, List<Integer>> districts; // 建筑
    private Status status = new Status();

    public CitadelPlayer(String userId) {
        this.userId = userId;
        this.score = 0; // 用来占位，否则redis不保存null
        this.visibleScore = 0; // 用来占位，否则redis不保存null
    }

    public static CitadelPlayer emptyPlayer() {
        CitadelPlayer p = new CitadelPlayer(null);

        p.score = 0;  // 用来占位，否则redis不保存null
        p.visibleScore = 0; // 用来占位，否则redis不保存null
        return p;
    }

    public void resetStatus() {
        this.status = new Status();
    }

    @Setter
    @Getter
    public static class Status {
        // choosing from 2/3 drawn district cards
        private boolean collecting = false;
        // finished collect action
        private boolean collected = false;
    }
}
