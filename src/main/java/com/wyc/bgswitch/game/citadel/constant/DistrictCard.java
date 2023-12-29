package com.wyc.bgswitch.game.citadel.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;

/**
 * @author wyc
 * {@link <a href="https://www.pinterest.ca/pin/271130840054368613/">csv file</a>)}
 * {@link <a href="https://boardgamegeek.com/image/1296141/citadels">images</a>}
 */
@Getter
public enum DistrictCard {
    Watchtower("Watchtower", 3, DistrictCardType.Military, 1, ""),
    Prison("Prison", 3, DistrictCardType.Military, 2, ""),
    Battlefield("Battlefield", 3, DistrictCardType.Military, 3, ""),
    Fortress("Fortress", 2, DistrictCardType.Military, 5, ""),
    Manor("Manor", 5, DistrictCardType.Noble, 3, ""),
    Castle("Castle", 4, DistrictCardType.Noble, 4, ""),
    Palace("Palace", 3, DistrictCardType.Noble, 5, ""),
    Tavern("Tavern", 5, DistrictCardType.Trade, 1, ""),
    Market("Market", 4, DistrictCardType.Trade, 2, ""),
    Trading_Post("Trading Post", 3, DistrictCardType.Trade, 2, ""),
    Docks("Docks", 3, DistrictCardType.Trade, 3, ""),
    Harbor("Harbor", 3, DistrictCardType.Trade, 4, ""),
    Town_Hall("Town Hall", 2, DistrictCardType.Trade, 5, ""),
    Temple("Temple", 3, DistrictCardType.Religious, 1, ""),
    Church("Church", 3, DistrictCardType.Religious, 2, ""),
    Monastery("Monastery", 3, DistrictCardType.Religious, 3, ""),
    Cathedral("Cathedral", 2, DistrictCardType.Religious, 5, ""),
    Haunted_City("Haunted City", 1, DistrictCardType.Special, 2, "For the purposes of victory points, the Haunted City is conisdered to be of the color of your choice.  You cannot use this ability if you built it during the last round of the game"),
    Keep("Keep", 2, DistrictCardType.Special, 3, "The Keep cannot be destroyed by the Warlord"),
    Laboratory("Laboratory", 1, DistrictCardType.Special, 5, "Once during your turn, you may discard a district card from your hand and receive one gold from the bank"),
    Smithy("Smithy", 1, DistrictCardType.Special, 5, "Once during your turn, you may pay two gold to draw 3 district cards."),
    Observatory("Observatory", 1, DistrictCardType.Special, 5, "If you choose to draw cards when you take an action, you draw 3 cards, keep one of your choice, and put the other 2 on the bottom of the deck"),
    Graveyard("Graveyard", 1, DistrictCardType.Special, 5, "When the Warlord destroys a district, you may pay one gold to take the destroyed district into your hand.  You may not do this if you are the Warlord"),
    Dragon_Gate("Dragon Gate", 1, DistrictCardType.Special, 6, "This district costs 6 gold to build, but is worth 8 points at the end of the game"),
    University("University", 1, DistrictCardType.Special, 6, "This district costs 6 gold to build, but is worth 8 points at the end of the game"),
    Library("Library", 1, DistrictCardType.Special, 6, "If you choose to draw cards you you take an action, you keep both of the cards you have drawn."),
    Great_Wall("Great Wall", 1, DistrictCardType.Special, 6, "The cost for the Warlord to destroy any of your other districts is increased by one gold"),
    Magic_School("Magic School", 1, DistrictCardType.Special, 6, "For the purposes of income, the School Of Magic I considered to be the color of your choice.  If you are the King this round, for example, the School is considered to be a noble (yellow) district."),
    ;
    private final String name;
    private final Integer count;

    private final Integer cost;
    private final DistrictCardType type;
    private final String description;


    DistrictCard(String name, Integer count, DistrictCardType type, Integer cost, String description) {
        this.name = name;
        this.count = count;
        this.cost = cost;
        this.type = type;
        this.description = description;
    }

    public static DistrictCard getCardByOrdinal(int id) {
        return DistrictCard.values()[id];
    }

    /**
     * get all cards
     *
     * @return
     */
    public static List<Integer> getAll() {
        List<Integer> cards = new ArrayList<>();
        for (DistrictCard card : DistrictCard.values()) {
            cards.addAll(Collections.nCopies(card.getCount(), card.ordinal()));
        }
        return cards;
    }

    public static String toFrontendConstantObject() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Arrays.stream(DistrictCard.values()).forEach(
                (o) -> sb.append("%s:{idx: %s, name: \"%s\", count: %s, cost: %s, type: \"%s\", description: \"%s\" },".formatted(
                        String.join("_", o.getName().split(" ")),
                        o.ordinal(),
                        String.join("_", o.getName().split(" ")),
                        o.getCount(),
                        o.getCost(),
                        o.getType(),
                        o.getDescription()
                ))
        );
        sb.append("}");
        return sb.toString();
    }

    @Getter
    public enum DistrictCardType {
        Noble("Noble", "yellow"),
        Religious("Religious", "blue"),
        Trade("Trade", "green"),
        Military("Military", "red"),
        Special("Special", "purple");
        private final String name;
        private final String color;

        DistrictCardType(String name, String color) {
            this.name = name;
            this.color = color;
        }

        public static String toFrontendConstantObject() {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            Arrays.stream(DistrictCardType.values()).forEach(
                    o -> sb.append("%s:{name: \"%s\", color:\"%s\"},".formatted(o.getName(), o.getName(), o.getColor()))
            );
            sb.append("}");
            return sb.toString();
        }
    }
}
