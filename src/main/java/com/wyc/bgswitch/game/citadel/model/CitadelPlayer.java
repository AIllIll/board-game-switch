package com.wyc.bgswitch.game.citadel.model;

import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.constant.DistrictCard;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@Data
public class CitadelPlayer {
    @NonNull
    private String userId;
    private Integer score; // 总得分
    private Integer visibleScore; // 可见得分
    private CitadelGameCharacter character1; // 身份1
    private CitadelGameCharacter character2; // 身份2
    private Integer coins; // 金币
    private List<Integer> hand; // 手牌
    private Map<DistrictCard.DistrictCardType, Integer> buildings; // 建筑
}
