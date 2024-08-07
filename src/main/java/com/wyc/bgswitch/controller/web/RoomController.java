package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.entity.RoomInfoVO;
import com.wyc.bgswitch.entity.UserInfo;
import com.wyc.bgswitch.service.RoomService;
import com.wyc.bgswitch.utils.debug.Debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Debug
    @GetMapping("/{roomId}")
    public RoomInfoVO getRoomInfo(@PathVariable String roomId) {
        return new RoomInfoVO(
                roomId,
                roomService.getRoomUserIds(roomId),
                roomService.getLastGameId(roomId)
        );
    }

    @Deprecated
    @GetMapping("/{roomId}/users")
    public List<UserInfo> getRoomUsers(@PathVariable String roomId) {
        return roomService.getRoomUserIds(roomId).stream().map(
                userId -> new UserInfo(userId, userId, null)
        ).collect(Collectors.toList());
    }

    @Deprecated
    @GetMapping("/{roomId}/game")
    public String getRoomGame(@PathVariable String roomId) {
        return roomService.getLastGameId(roomId);
    }
}
