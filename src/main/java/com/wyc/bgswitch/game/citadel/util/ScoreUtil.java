package com.wyc.bgswitch.game.citadel.util;

import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * @author wyc
 * @link <a href="https://zhuanlan.zhihu.com/p/492557646">...</a>
 */
public class ScoreUtil {
    public static int computeVisibleScore(CitadelPlayer player) {
        int score = 0;
        // score of districts
        if (player.getDistricts() != null) {
            Map<DistrictCard.DistrictCardType, List<DistrictCard>> districtMap = DistrictUtil.convertListToMap(player.getDistricts());
            for (DistrictCard.DistrictCardType t : DistrictCard.DistrictCardType.values()) {
                List<DistrictCard> districtsOfOneColor = districtMap.getOrDefault(t, Collections.emptyList());
                score += districtsOfOneColor.stream().map(DistrictCard::getCost).reduce(Integer::sum).orElse(0);
            }
            if (districtMap.values().stream().filter(l -> l != null && l.size() > 0).toList().size() == 5) {
                score += extraScore.ALL_COLORS.getPoints();
            }
        }

        return score;
    }

    public static int computeScore(CitadelPlayer player) {
        // todo
        return computeVisibleScore(player);
    }

    @Getter
    private enum extraScore {
        // todo: points from special district card
        FIRST_PLAYER_WITH_8_DISTRICTS(4),
        OTHER_PLAYER_WITH_8_DISTRICTS(2),
        ALL_COLORS(3);

        private final Integer points;

        extraScore(Integer points) {
            this.points = points;
        }
    }
}
