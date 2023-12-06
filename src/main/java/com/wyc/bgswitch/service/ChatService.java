package com.wyc.bgswitch.service;

import com.wyc.bgswitch.entity.ChatMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final SimpMessagingTemplate messaging;
    private final RoomService roomService;
    @Value("${prefix.ws.channels.chat}")
    private String channelPrefixChat;

    @Autowired
    ChatService(SimpMessagingTemplate messaging, RoomService citadelService) {
        this.messaging = messaging;
        this.roomService = citadelService;
    }

    public void sendToRoom(String room, ChatMessage msg) {
        // 提供标记，给前端判断消息的房间，无需通过地址判断
        msg.setToRoom(room);
        List<String> userList = roomService.getRoomUsers(room);
        for (String receiver : userList) {
            messaging.convertAndSendToUser(receiver, String.format("%s/room/%s", channelPrefixChat, room), msg);
        }
    }

    public void sendToLobby(String receiver, ChatMessage msg) {
        messaging.convertAndSendToUser(receiver, String.format("%s/lobby", channelPrefixChat), msg);
    }

    public void sendToUser(String receiver, ChatMessage msg) {
        messaging.convertAndSendToUser(receiver, String.format("%s/user", channelPrefixChat), msg);
    }
}
