package com.wyc.bgswitch.controller.websocket;

import com.alibaba.fastjson.JSONObject;
import com.wyc.bgswitch.entity.ChatMessage;
import com.wyc.bgswitch.service.ChatService;
import com.wyc.bgswitch.service.RoomService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import jakarta.annotation.security.RolesAllowed;

@Controller
@MessageMapping("/citadel")
public class CitadelWsController {

    private final ChatService chatService;
    private final RoomService roomService;

    @Autowired
    CitadelWsController(ChatService chatService, RoomService citadelService) {
        this.chatService = chatService;
        this.roomService = citadelService;
    }


    @MessageMapping("/room")
    @RolesAllowed("USER")
    public void onUserEnterRoom(@Payload JSONObject payload, Authentication authentication) {
        String room = (String) payload.get("room");
        String user = authentication.getName();
        System.out.println(room);
        System.out.println(user);
        roomService.userEnterRoom(user, room);
        ChatMessage msg = new ChatMessage();
        msg.setContent(String.format("%s enter room %s.", user, room));
        msg.setIsSystemMsg(true);
        chatService.sendToRoom(room, msg);
    }
}
