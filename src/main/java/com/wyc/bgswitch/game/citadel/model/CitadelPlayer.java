package com.wyc.bgswitch.game.citadel.model;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.util.DistrictUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wyc
 */
@Data
public class CitadelPlayer implements Cloneable {
    private String userId;
    private Integer score = 0; // 总得分
    private Integer visibleScore = 0; // 可见得分
    private List<CitadelGameCharacter> characters = new ArrayList<>(); // 身份
    private Integer coins = 0; // 金币
    private List<Integer> hand = new ArrayList<>(); // 手牌
    private List<Integer> drawnCards; // 抽的牌
    private List<Integer> districts = new ArrayList<>(); // 建筑
    private Status status = new Status(); // status reset every character turn

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

    public Map<Integer, List<Integer>> getDistrictMap() {
        return DistrictUtil.convertListToIdMap(districts);
    }

    public void resetStatus() {
        this.status = new Status();
    }

    @Override
    public CitadelPlayer clone() {
        try {
            CitadelPlayer clone = (CitadelPlayer) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Setter
    @Getter
    public static class Status {
        // choosing from 2/3 drawn district cards
        private boolean collecting = false;
        // finished collect action
        private boolean collected = false;
        // times to build
        private int buildTimes = 1;
        // number of draw
        private int draw = 2;
        // number of keep
        private int keep = 1;
        // defense: extra cost (base on district cost) to destroy this player's districts, default is -1
        private int defense = -1;

        public void costBuildTimes() {
            buildTimes -= 1;
        }
    }
}
