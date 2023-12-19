package com.wyc.bgswitch.controller.web.game;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.game.citadel.CitadelGameConfig;
import com.wyc.bgswitch.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/game/citadel")
public class CitadelGameController {
    private final RoomService roomService;

    @Autowired
    public CitadelGameController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public String createGame(@RequestBody CreateGameRequestBody body) {
        String roomId = body.roomId();
        String gameId = "/citadel/" + roomId; // todo create game
        roomService.setRoomGame(roomId, "game");
        return gameId;
    }

    @GetMapping("/{gameId}")
    public String getGame(@PathVariable String gameId) {
        return "game: " + gameId;
    }


    public record CreateGameRequestBody(String roomId, CitadelGameConfig config) {
    }

}
