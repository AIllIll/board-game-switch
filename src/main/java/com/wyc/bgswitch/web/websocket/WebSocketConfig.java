package com.wyc.bgswitch.web.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 发布/订阅式的channel
        config.setApplicationDestinationPrefixes("/bgs"); // app应用，可以向restful一样向server发东西
        config.setUserDestinationPrefix("/user"); // 这是对单个用户发消息
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/bgs-websocket");
        registry.addEndpoint("/bgs-sockjs").withSockJS();
        // 对于不支持websocket的browser的备选方案，注意这和上面一行是独立的，必须写成两个，并不是在bgs-websocket上附加对sockjs的支持
    }

}
