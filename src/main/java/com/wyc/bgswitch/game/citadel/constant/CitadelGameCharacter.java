package com.wyc.bgswitch.game.citadel.constant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author wyc
 */

@Getter
public enum CitadelGameCharacter {
    ASSASSIN("ASSASSIN", List.of(Ability.Assassinate), "Announce the title of another character that you wish to murder. The player who has the murdered character must say nothing, and must remain silent when the murdered character is called upon to take his turn. The murdered character misses his entire turn."),
    THIEF("THIEF", List.of(Ability.Steal), "Announce the title of a character from whom you wish to steal. When the player who has that character is called upon to take his turn, you first take all of his gold. You may not steal from the Assassin or the Assassin’s target."),
    MAGICIAN("MAGICIAN", List.of(Ability.Magic), "At any time during your turn, you have one of two options: • Exchange your entire hand of cards (not the cards in your city) with the hand of another player (this applies even if you have no cards in your hand, in which case you simply take the other player’s cards). • Place any number of cards from your hand facedown at the bottom of the District Deck, and then draw an equal number of cards from the top of the District Deck."),
    KING("KING", List.of(Ability.Collect), "You receive one gold for each noble (yellow) district in your city. When the King is called, you immediately receive the Crown. You now call the characters, and you will be the first player to choose your character during the next round. If there is no King during the next round, you keep the Crown. If you are murdered, you skip your turn like any other character. Nevertheless, after the last player has played his turn, when it becomes known that you had the murdered King’s character card, you take the Crown (as the King’s heir)."),
    BISHOP("BISHOP", List.of(Ability.Collect), "You receive one gold for each religious (blue) district in your city. Your districts may not be destroyed/exchanged by the Warlord/Diplomat"),
    MERCHANT("MERCHANT", List.of(Ability.Collect), "You receive one gold for each trade (green) district in your city. After you take an action, you receive one additional gold."),
    ARCHITECT("ARCHITECT", Collections.emptyList(), "After you take an action, you draw two additional district cards and put both in your hand. You may build up to three districts during your turn."),
    WARLORD("WARLORD", List.of(Ability.Collect, Ability.Destroy), "You receive one gold for each military (red) district in your city. At the end of your turn, you may destroy one district of your choice by paying a number of gold equal to one less than the cost of the district. Thus, you may destroy a cost one district for free, a cost two district for one gold, or a cost six district for five gold, etc. You may destroy one of your own districts. You may not, however, destroy a district in a city that is already completed by having eight districts (or seven districts when the Bell Tower is in play)."),
    ;
    private final String name;
    private final List<Ability> abilities;
    private final String description;

    CitadelGameCharacter(String name, List<Ability> abilities, String description) {
        this.name = name;
        this.abilities = abilities;
        this.description = description;
    }

    public static String toFrontendConstantObject() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Arrays.stream(CitadelGameCharacter.values()).forEach(
                o -> sb.append("%s:{idx: %s, name: '%s', abilities:%s, description:'%s'},".formatted(
                        o.getName(),
                        o.ordinal(),
                        o.getName(),
                        o.getAbilities().stream().map(a -> "'%s'".formatted(a.name)).toList(),
                        o.getDescription()
                ))
        );
        sb.append("}");
        return sb.toString();
    }

    public enum CardStatus {
        AVAILABLE,
        PICKED,
        HIDDEN, // in dual game, one character will be preserved fore second player to pick
        BURIED, // buried
        UNAVAILABLE
    }

    @Getter
    public enum Ability {
        // Assassin
        Assassinate("Assassinate"),
        // Thief
        Steal("Steal"),
        // Magician
        Magic("Magic"), // Exchange or DiscardAndDraw
        // King、Bishop、Merchant、Warlord
        Collect("Collect"),
        // Warlord
        Destroy("Destroy"),
        ;
        private final String name;

        Ability(String name) {
            this.name = name;
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class InGameStatus {
        private boolean assassinated = false; // killed by assassin
        private boolean stolen = false; // stolen by thief
        private boolean[] abilityUsed; // whether abilities have been used
        private boolean over = false; // the character's turn is over

        public InGameStatus(CitadelGameCharacter character) {
            if (character.equals(WARLORD)) {
                abilityUsed = new boolean[]{false, false};
            } else if (List.of(ASSASSIN, THIEF, MAGICIAN, KING, BISHOP, MERCHANT).contains(character)) {
                abilityUsed = new boolean[]{false};
            } else if (ARCHITECT.equals(character)) {
                abilityUsed = new boolean[]{};
            }
        }
    }
}
