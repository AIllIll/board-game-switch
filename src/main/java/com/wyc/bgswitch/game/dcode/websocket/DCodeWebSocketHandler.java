package com.wyc.bgswitch.game.dcode.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class DCodeWebSocketHandler extends TextWebSocketHandler {

    private final SessionManager sessionManager;

    @Autowired
    public DCodeWebSocketHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.printf("66666666666666666666, WebSocket received: %s%n", message);
        System.out.printf("77777777777777777777, %s%n", session.getPrincipal());

        session.sendMessage(new TextMessage("444"));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        sessionManager.addSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionManager.removeSession(session);
    }
}