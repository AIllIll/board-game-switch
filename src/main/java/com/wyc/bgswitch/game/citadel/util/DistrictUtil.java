package com.wyc.bgswitch.game.citadel.util;

import com.wyc.bgswitch.game.citadel.constant.DistrictCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wyc
 */
public class DistrictUtil {
    public static Map<DistrictCard.DistrictCardType, List<DistrictCard>> convertListToMap(List<Integer> districts) {
        if (districts == null) {
            return null;
        }
        Map<DistrictCard.DistrictCardType, List<DistrictCard>> districtMap = new HashMap<>();
        List<DistrictCard> cardList = districts.stream().map(id -> DistrictCard.values()[id]).toList();
        for (DistrictCard.DistrictCardType type : DistrictCard.DistrictCardType.values()) {
            districtMap.put(type, cardList.stream().filter(card -> card.getType().equals(type)).sorted().toList());
        }
        return districtMap;
    }

    public static Map<Integer, List<Integer>> convertListToIdMap(List<Integer> districts) {
        if (districts == null) {
            return null;
        }
        Map<DistrictCard.DistrictCardType, List<DistrictCard>> districtMap = convertListToMap(districts);
        Map<Integer, List<Integer>> map = new HashMap<>();
        for (DistrictCard.DistrictCardType cardType : districtMap.keySet()) {
            map.put(cardType.ordinal(), districtMap.get(cardType).stream().map(DistrictCard::ordinal).toList());
        }
        return map;
    }

    public static List<Integer> convertMapToList(Map<DistrictCard.DistrictCardType, List<DistrictCard>> districtMap) {
        if (districtMap == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        for (DistrictCard.DistrictCardType type : districtMap.keySet()) {
            list.addAll(districtMap.get(type).stream().map(Enum::ordinal).toList());
        }
        return list;
    }
}
