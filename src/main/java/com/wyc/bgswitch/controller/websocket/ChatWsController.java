package com.wyc.bgswitch.controller.websocket;

import com.wyc.bgswitch.entity.ChatMessage;
import com.wyc.bgswitch.service.message.ChatMessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;

/**
 * @author wyc
 */
@Controller
@MessageMapping("/chat")
public class ChatWsController {

    private final SimpMessagingTemplate messaging;

    private final ChatMessageService chatService;

    @Autowired
    ChatWsController(SimpMessagingTemplate messaging, ChatMessageService chatService) {
        this.messaging = messaging;
        this.chatService = chatService;
    }

    @MessageMapping("/lobby")
    @PreAuthorize("hasAnyRole('USER')")
    public void sendToLobby(ChatMessage msg, Principal principal) {
        msg.setFromUser(principal.getName());
        msg.setToLobby(true);
        messaging.convertAndSend("/public/lobby", msg);
    }

    @MessageMapping("/user")
    public void sendToUser(ChatMessage msg, Principal principal) {
        System.out.printf(
                "[%s] Received msg from %s to %s: %s%n",
                new Date(msg.getCreatedAt()),
                msg.getFromUser(),
                msg.getToUser(),
                msg.getContent()
        );
        msg.setFromUser(principal.getName());
        chatService.sendToUser(msg.getToUser(), msg);
    }

    @MessageMapping("/room/{roomId}")
    public void sendToRoom(@DestinationVariable String roomId, ChatMessage msg, Principal principal) {
        System.out.printf(
                "[%s] Received msg from %s to room %s: %s%n",
                new Date(msg.getCreatedAt()),
                msg.getFromUser(),
                roomId,
                msg.getContent()
        );
        msg.setFromUser(principal.getName());
        chatService.sendToRoom(roomId, msg);
    }
}
