package com.wyc.bgswitch.game.dcode.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SessionManager {
    public List<WebSocketSession> sessions = new ArrayList<WebSocketSession>();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    public void notifyAllPlayer() {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage("update"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
