package com.wyc.bgswitch.game.citadel.util;

import com.wyc.bgswitch.game.citadel.constant.DistrictCard;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.Getter;

/**
 * @author wyc
 * @link <a href="https://zhuanlan.zhihu.com/p/492557646">...</a>
 */
public class ScoreUtil {
    public static int computeVisibleScore(CitadelGame game, CitadelPlayer player) {
        int score = 0;
        // score of districts
        if (player.getDistricts() != null) {
            Map<DistrictCard.DistrictCardType, List<DistrictCard>> districtMap = DistrictUtil.convertListToMap(player.getDistricts());
            for (DistrictCard.DistrictCardType t : DistrictCard.DistrictCardType.values()) {
                List<DistrictCard> districtsOfOneColor = districtMap.getOrDefault(t, Collections.emptyList());
                score += districtsOfOneColor.stream().map(DistrictCard::getCost).reduce(Integer::sum).orElse(0);
            }
            // extra score for all colors.
            int numOfColor = districtMap.values().stream().filter(l -> l != null && l.size() > 0).toList().size();
            boolean fourAsFive = player.getDistricts().contains(DistrictCard.Haunted_City.ordinal()) &&
                    player.getDistrictMap().get(DistrictCard.DistrictCardType.Special.ordinal()).size() >= 2;
            if (numOfColor == 5 || numOfColor == 4 && fourAsFive) {
                score += extraScore.ALL_COLORS.getPoints();
            }
            // score for building 8 districts
            if (player.getDistricts().size() >= 8) {
                score += extraScore.PLAYER_WITH_8_DISTRICTS.getPoints();
            }
            // score for building 8 districts first
            if (game.getFirstPlace() != -1 && game.getPlayers().get(game.getFirstPlace()).getUserId().equals(player.getUserId())) {
                score += extraScore.FIRST_PLAYER_WITH_8_DISTRICTS.getPoints();
            }
            // score for Dragon_Gate
            if (player.getDistricts().contains(DistrictCard.Dragon_Gate.ordinal())) {
                score += 2;
            }
            // score for University
            if (player.getDistricts().contains(DistrictCard.University.ordinal())) {
                score += 2;
            }
        }

        return score;
    }

    public static int computeScore(CitadelGame game, CitadelPlayer player) {
        // todo
        return computeVisibleScore(game, player);
    }

    @Getter
    private enum extraScore {
        FIRST_PLAYER_WITH_8_DISTRICTS(2),
        PLAYER_WITH_8_DISTRICTS(2),
        ALL_COLORS(3);

        private final Integer points;

        extraScore(Integer points) {
            this.points = points;
        }
    }
}
