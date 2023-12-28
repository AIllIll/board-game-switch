package com.wyc.bgswitch.controller.websocket;

import com.wyc.bgswitch.entity.ChatMessage;
import com.wyc.bgswitch.service.RoomService;
import com.wyc.bgswitch.service.message.ChatMessageService;
import com.wyc.bgswitch.service.message.RoomMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import jakarta.annotation.security.RolesAllowed;

/**
 * @author wyc
 */
@Controller
@MessageMapping("/room")
public class RoomWsController {

    private final ChatMessageService chatService;
    private final RoomService roomService;
    private final RoomMessageService roomMessageService;

    @Autowired
    RoomWsController(ChatMessageService chatService, RoomService citadelService, RoomMessageService roomMessageService) {
        this.chatService = chatService;
        this.roomService = citadelService;
        this.roomMessageService = roomMessageService;
    }


    @MessageMapping("/{roomId}/enter")
    @RolesAllowed("USER")
    public void onUserEnterRoom(@DestinationVariable String roomId, Authentication authentication) {
        String user = authentication.getName();
        System.out.println(roomId);
        System.out.println(user);
        roomService.userEnterRoom(user, roomId);
        roomMessageService.notifyUpdate(roomService.getRoomInfo(roomId));
        ChatMessage msg = new ChatMessage();
        msg.setContent(String.format("%s enter room %s.", user, roomId));
        msg.setIsSystemMsg(true);
        chatService.sendToRoom(roomId, msg);
    }
}
