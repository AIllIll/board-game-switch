package com.wyc.bgswitch.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // todo 解耦，改为通过父类获取
    private final TextWebSocketHandler myHandler;

    private final HandshakeHandler handshakeHandler;

    @Autowired
    public WebSocketConfig(TextWebSocketHandler myHandler, HandshakeHandler handshakeHandler) {
        this.myHandler = myHandler;
        this.handshakeHandler = handshakeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler, "/common-websocket")
                .setHandshakeHandler(handshakeHandler);
    }



}