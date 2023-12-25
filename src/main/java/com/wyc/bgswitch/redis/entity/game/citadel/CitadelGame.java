package com.wyc.bgswitch.redis.entity.game.citadel;

import com.wyc.bgswitch.game.citadel.model.CitadelGameAction;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.game.constant.GameStatus;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

/**
 * @author wyc
 */
@RedisHash("bgs/repo/game/citadel")
@Data
public class CitadelGame {
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
    private Integer currentCharacter; // 当前角色序号
    private Long createdAt;
    private Long startedAt;
    private Long finishedAt;

    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, String hostId) {
        this.roomId = roomId;
        this.config = config;
        this.status = GameStatus.PREPARE;
    }

    public CitadelGame(@NonNull String roomId, @NonNull CitadelGameConfig config, String hostId, List<CitadelPlayer> players) {
        this.roomId = roomId;
        this.config = config;
        this.players = players;
        this.status = GameStatus.PREPARE;
    }

    public String getIdWithGamePrefix() {
        return "citadel:%s".formatted(id);
    }

}
