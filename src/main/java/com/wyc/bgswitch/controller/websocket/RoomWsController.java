package com.wyc.bgswitch.controller.websocket;

import com.wyc.bgswitch.entity.ChatMessage;
import com.wyc.bgswitch.service.ChatMessageService;
import com.wyc.bgswitch.service.RoomService;

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

    @Autowired
    RoomWsController(ChatMessageService chatService, RoomService citadelService) {
        this.chatService = chatService;
        this.roomService = citadelService;
    }


    @MessageMapping("/{roomId}/enter")
    @RolesAllowed("USER")
    public void onUserEnterRoom(@DestinationVariable String roomId, Authentication authentication) {
        String user = authentication.getName();
        System.out.println(roomId);
        System.out.println(user);
        roomService.userEnterRoom(user, roomId);
        ChatMessage msg = new ChatMessage();
        msg.setContent(String.format("%s enter room %s.", user, roomId));
        msg.setIsSystemMsg(true);
        chatService.sendToRoom(roomId, msg);
    }
}
