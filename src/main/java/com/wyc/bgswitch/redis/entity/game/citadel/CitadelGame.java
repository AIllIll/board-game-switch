package com.wyc.bgswitch.redis.entity.game.citadel;

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
    private GameStatus status; // 游戏状态
    private List<Integer> cardDeck; // 牌堆
    private Integer crown; // 皇冠：玩家序号
    private Integer heir; // 继承人：玩家序号
    private Integer round; // 回合数
    private Integer pickingTurn; // 当前选人玩家序号, 如果超过玩家数量2倍，则说明进入角色环节
    private List<CitadelGameCharacter.CardStatus> characterCardStatus; // 角色卡状态
    private List<CitadelGameCharacter.InGameStatus> characterStatus; // 角色状态
    private Integer firstFinishedPlayer; // 第一个完成建筑的玩家
    private Long createdAt;
    private Long startedAt;
    private Long finishedAt;

    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, @NonNull String hostId) {
        this.roomId = roomId;
        this.config = config;
        this.status = GameStatus.PREPARE;
        this.players = new ArrayList<>(Collections.nCopies(config.getPlayerNumber(), CitadelPlayer.emptyPlayer())); // 创建空座位
        this.hostId = hostId;
    }

    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, @NonNull String hostId, List<CitadelPlayer> players) {
        this.roomId = roomId;
        this.config = config;
        this.hostId = hostId;
        this.players = players;
        this.status = GameStatus.PREPARE;
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
}
