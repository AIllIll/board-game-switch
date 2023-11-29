package com.wyc.bgswitch.controller.websocket;

import com.wyc.bgswitch.entities.ChatMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@MessageMapping("/chat")
public class ChatWsController {

    private final SimpMessagingTemplate messaging;

    @Autowired
    public ChatWsController(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @MessageMapping("/lobby")
    public void sendToLobby(ChatMessage msg, Principal principal) {
        msg.setFromUser(principal.getName());
        msg.setToLobby(true);
        messaging.convertAndSend("/public/lobby", msg);
    }

    @MessageMapping("/user/{user}")
    public void sendToUser(ChatMessage msg, Principal principal, @DestinationVariable("user") String toUser) {
        System.out.println(666);
        System.out.println(toUser);
        System.out.println(msg);
        msg.setFromUser(principal.getName());
        msg.setToUser(toUser);
        messaging.convertAndSendToUser(toUser, "/private/chat", msg);
    }

    @MessageMapping("/room/{room}")
    public void sendToRoom(ChatMessage msg, Principal principal, @DestinationVariable String room) {
        msg.setFromUser(principal.getName());
        msg.setToRoom(room);
        // todo: getRoomUsers and send
        // todo: keep msg in room record
        List<String> userList = new ArrayList<>();
        for (String user : userList) {
            messaging.convertAndSendToUser(user, "/private/chat", msg);
        }
    }
}
