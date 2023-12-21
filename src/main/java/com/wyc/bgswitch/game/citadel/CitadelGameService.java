package com.wyc.bgswitch.game.citadel;

import com.wyc.bgswitch.game.citadel.model.CitadelGameConfig;
import com.wyc.bgswitch.game.citadel.model.CitadelPlayer;
import com.wyc.bgswitch.redis.entity.Room;
import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;
import com.wyc.bgswitch.redis.repository.CitadelGameRepository;
import com.wyc.bgswitch.redis.repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;

/**
 * @author wyc
 */
@Service
public class CitadelGameService {

    private final CitadelGameRepository gameRepo;
    private final RoomRepository roomRepo;

    @Autowired
    public CitadelGameService(CitadelGameRepository gameRepo, RoomRepository roomRepo) {
        this.gameRepo = gameRepo;
        this.roomRepo = roomRepo;
    }

    public String create(String roomId, CitadelGameConfig config) {
        Room room = roomRepo.findById(roomId).orElse(null);
        Assert.notNull(room, String.format("Room[%s] not found.", roomId));
        CitadelPlayer host = new CitadelPlayer(room.getUsers().get(0), 0);
        CitadelGame game = gameRepo.save(new CitadelGame(roomId, config, Collections.singletonList(host)));
        return game.getId();
    }

    public CitadelGame get(String gameId) {
        return gameRepo.findById(gameId).orElse(null);
    }

    public void update(CitadelGame game) {
        gameRepo.save(game);
    }

    public void findByIds(Iterable<String> ids) {
        gameRepo.findAllById(ids);
    }

    public void findAll() {
        gameRepo.findAll();
    }

    public void removeAll() {
        gameRepo.deleteAll();
    }
}
