package com.wyc.bgswitch.game.citadel;

import com.wyc.bgswitch.config.lock.RedisLockPrefix;
import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.lock.LockManager;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;
import com.wyc.bgswitch.redis.repository.CitadelGameRepository;
import com.wyc.bgswitch.redis.zset.RoomGamesZSetManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyc
 */
@Service
public class CitadelGameService {
    private final CitadelGameRepository gameRepo;
    private final RoomGamesZSetManager roomGamesZSetManager;
    private final LockManager lockManager;

    @Autowired
    public CitadelGameService(
            CitadelGameRepository gameRepo,
            RoomGamesZSetManager roomGamesZSetManager,
            LockManager lockManager
    ) {
        this.gameRepo = gameRepo;
        this.roomGamesZSetManager = roomGamesZSetManager;
        this.lockManager = lockManager;
    }

    /**
     * create citadel game
     *
     * @param roomId
     * @param config
     * @return gameId
     */
    public String create(String roomId, CitadelGameConfig config, String hostId) {
        CitadelGame game = gameRepo.save(new CitadelGame(roomId, config, hostId));
        return game.getIdWithGamePrefix();
    }

    /**
     * find game by id
     *
     * @param gameId
     * @return
     */
    public CitadelGame get(String gameId) {
        if (gameId == null) {
            return null;
        }
        return gameRepo.findById(CitadelGame.getPureId(gameId)).orElse(null);
    }

    /**
     * update game atomically
     *
     * @param gameId
     * @param game
     */

    public void update(String gameId, CitadelGame game) {
        LockManager.MultiLockBuilder.MultiLock lock = lockManager.useBuilder().obtain(RedisLockPrefix.LOCK_PREFIX_GAME).of(game.getId()).build();
        lock.lock();
        try {
            game.setId(gameId);
            gameRepo.save(game);
        } finally {
            lock.unLock();
        }
    }

    public List<CitadelGame> getRoomGames(String roomId) {
        return gameRepo.findByRoomId(roomId);
    }

    public void findByIds(Iterable<String> ids) {
        gameRepo.findAllById(ids);
    }

    public void findAll() {
        gameRepo.findAll();
    }

    public void removeAll() {
//        gameRepo.deleteAll();
    }
}
