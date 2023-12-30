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
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author wyc
 */
@RedisHash("bgs/repo/game/citadel")
@Data
@NoArgsConstructor(force = true)
public class CitadelGame {

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
    private List<CitadelPlayer> players; // 玩家列表
    private List<CitadelGameAction> actions; // 玩家行为记录
    private GameStatus status = GameStatus.PREPARE; // 游戏状态
    private List<Integer> cardDeck; // 牌堆
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
    private Integer firstFinishedPlayer; // 第一个完成建筑的玩家
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
        this.setCharacterStatus(new ArrayList<>(Collections.nCopies(8, new CitadelGameCharacter.InGameStatus())));
    }

    public Long getRandomSeed() {
        return this.getCreatedAt();
    }

    @JsonIgnore
    public CitadelPlayer getCurrentPlayer() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        if (turn < 2 * numOfPlayers) {
            return this.getPlayers().get((this.getCrown() + turn) % numOfPlayers);
        } else {
            int currentCharacterIdx = turn - 2 * numOfPlayers;
            CitadelGameCharacter character = CitadelGameCharacter.values()[currentCharacterIdx];
            CitadelPlayer player = this.getPlayers().stream().filter(
                    p -> p.getCharacters().contains(character)
            ).findAny().orElse(null);
            return player;
        }
    }

    public String getCurrentPlayerId() {
        CitadelPlayer player = getCurrentPlayer();
        return player == null ? null : player.getUserId();
    }


    @JsonIgnore
    public Boolean isInPickingTurn() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        return turn < 2 * numOfPlayers;
    }

    @JsonIgnore
    public Boolean isInCharacterTurn() {
        int turn = this.getTurn();
        int numOfPlayers = this.getPlayers().size();
        return turn >= 2 * numOfPlayers;
    }
}
