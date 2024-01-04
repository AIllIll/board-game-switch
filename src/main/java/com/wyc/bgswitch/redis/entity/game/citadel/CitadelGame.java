package com.wyc.bgswitch.redis.entity.game.citadel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wyc.bgswitch.game.citadel.constant.CitadelGameCharacter;
import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.constant.GameStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author wyc
 */
@RedisHash("bgs/repo/game/citadel")
@Data
@NoArgsConstructor(force = true)
public class CitadelGame implements Cloneable {

    public static final String GAME_PREFIX = "citadel/";

    @Id
    private String id;
    @Indexed
    @NonNull
    private String roomId;
    @NonNull
    private CitadelGameConfig config;
    @NonNull
    private String hostId; // 房主
    private List<CitadelPlayer> players = new ArrayList<>(); // 玩家列表
    private List<CitadelGameAction> actions = new ArrayList<>(); // 玩家行为记录
    private GameStatus status = GameStatus.PREPARE; // 游戏状态
    private List<Integer> cardDeck = new ArrayList<>(); // 牌堆
    private Integer crown = 0; // 皇冠：玩家序号
    private Integer heir = 0; // 继承人：玩家序号
    private Integer round = 0; // 回合数
    /**
     * turn 轮次序号
     * <p>
     * 选角色： [0, 2 * players.size)
     * 角色行动： [2 * players.size, 2 * players.size + 8), 如果角色不存在就直接跳轮
     */
    private Integer turn = 0; // 轮次序号
    /**
     * turn 轮次序号
     * <p>
     * 选角色： [0, 2 * players.size)
     * 角色行动： [2 * players.size, 2 * players.size + 8), 如果角色不存在就直接跳轮
     */
    private List<CitadelGameCharacter.CardStatus> characterCardStatus; // 角色卡状态
    private List<CitadelGameCharacter.InGameStatus> characterStatus; // 角色状态
    private Integer firstPlace; // 第一个完成建筑的玩家
    private Long createdAt;
    private Long startedAt;
    private Long finishedAt;

    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, @NonNull String hostId) {
        this.roomId = roomId;
        this.config = config;
        this.hostId = hostId;
        this.players = new ArrayList<>(Collections.nCopies(config.getPlayerNumber(), CitadelPlayer.emptyPlayer())); // 创建空座位
    }

    @Deprecated
    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, @NonNull String hostId, List<CitadelPlayer> players) {
        this.roomId = roomId;
        this.config = config;
        this.hostId = hostId;
        this.players = players;
    }

    public static String getPureId(String id) {
        return id.replace(GAME_PREFIX, "");
    }

    public String getIdWithGamePrefix() {
        return "%s%s".formatted(GAME_PREFIX, id);
    }

    public void clearCharacterCardStatus() {
        this.setCharacterCardStatus(new ArrayList<>(Collections.nCopies(8, CitadelGameCharacter.CardStatus.AVAILABLE)));
    }

    public void clearCharacterStatus() {
        this.setCharacterStatus(new ArrayList<>(
                Arrays.stream(CitadelGameCharacter.values()).map(CitadelGameCharacter.InGameStatus::new).toList()
        ));
    }

    public Long getRandomSeed() {
        // 每个回合是不一样的随机数
        return this.getCreatedAt() * this.round;
    }

    public int getCurrentPlayerIdx() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        if (isInPickingTurn()) {
            return (this.getCrown() + turn) % numOfPlayers;
        } else if (isInCharacterTurn()) {
            int currentCharacterIdx = getCurrentCharacterIdx();
            CitadelGameCharacter character = CitadelGameCharacter.values()[currentCharacterIdx];
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getCharacters().contains(character)) {
                    return i;
                }
            }
            return -1;
        }
        return -1;
    }

    @JsonIgnore
    public CitadelPlayer getCurrentPlayer() {
        int idx = getCurrentPlayerIdx();
        if (idx >= 0 && idx < players.size()) {
            return players.get(idx);
        } else {
            return null;
        }
    }

    public String getCurrentPlayerId() {
        CitadelPlayer player = getCurrentPlayer();
        return player == null ? null : player.getUserId();
    }

    public int getCurrentCharacterIdx() {
        if (!isInCharacterTurn()) {
            return -1;
        }
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        return turn - 2 * numOfPlayers;
    }

    public Boolean isInPickingTurn() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        return this.status.equals(GameStatus.ONGOING) && turn < 2 * numOfPlayers && turn >= 0;
    }

    public Boolean isInCharacterTurn() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        return this.status.equals(GameStatus.ONGOING) && turn >= 2 * numOfPlayers && turn - 2 * numOfPlayers < 8;
    }

    @JsonIgnore
    public Boolean isCharacterTurnOf(CitadelGameCharacter character) {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        int characterTurn = turn - 2 * numOfPlayers;
        return characterTurn == character.ordinal();
    }

    @Override
    public CitadelGame clone() {
        try {
            CitadelGame game = (CitadelGame) super.clone();
            game.setPlayers(game.getPlayers().stream().map(CitadelPlayer::clone).toList());
            return game;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public CitadelGame masked(String userId) {
        CitadelGame game = this.clone();
        // 隐藏用户信息
        game.getPlayers().forEach(p -> {
            if (!Objects.equals(p.getUserId(), userId)) {
                // 隐藏用户角色
                int currentCharacterIdx = game.getCurrentCharacterIdx();
                if (p.getCharacters() != null) {
                    p.setCharacters(p.getCharacters().stream().map(c -> c.ordinal() > currentCharacterIdx ? null : c).toList());
                }
                // 隐藏手牌
                if (p.getHand() != null) {
                    p.setHand(Collections.nCopies(p.getHand().size(), null));
                }
                // 隐藏真实分数
                p.setScore(0);
            }
        });
        // 隐藏牌堆
        game.setCardDeck(game.getCardDeck().stream().map(c -> -1).toList());
        return game;

    }


}
