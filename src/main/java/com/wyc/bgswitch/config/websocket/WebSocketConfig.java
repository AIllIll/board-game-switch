package com.wyc.bgswitch.config.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${prefix.ws.application.destination}")
    private String appDestinationPrefix;
    @Value("${prefix.ws.user.destination}")
    private String userDestinationPrefix;
    @Value("${prefix.ws.endpoint.websocket}")
    private String websocketEndpoint;
    @Value("${prefix.ws.endpoint.sockjs}")
    private String sockjsEndpoint;

    @Autowired
    private ApplicationContext context;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(
                "/public", // 默认
                "/private" // 通用
        ); // 发布/订阅式的channel
        config.setApplicationDestinationPrefixes(appDestinationPrefix); // app应用，可以向restful一样向server发东西
        config.setUserDestinationPrefix(userDestinationPrefix); // 这是对单个用户发消息
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(websocketEndpoint).setAllowedOrigins("*");
        registry.addEndpoint(sockjsEndpoint).setAllowedOrigins("*").withSockJS();
        // 对于不支持websocket的browser的备选方案，注意这和上面一行是独立的，必须写成两个，并不是在bgs-websocket上附加对sockjs的支持
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {

        // 要求通过Authentication
        AuthorizationManager<Message<?>> myAuthorizationRules = AuthenticatedAuthorizationManager.authenticated();
        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(myAuthorizationRules);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(this.context);
        authz.setAuthorizationEventPublisher(publisher);

        // 要求具备身份
        AuthorizationManager<Message<?>> messageAuthorizationManager = MessageMatcherDelegatingAuthorizationManager.builder()
                .simpDestMatchers("/private", "/private/**").denyAll() // 禁用所有 /private
                .simpMessageDestMatchers("/public", "/public/**").denyAll() // 禁止SEND /public
                .anyMessage().hasRole("USER")
//                .anyMessage().hasAuthority("SCOPE_ROLE_USER")
                .build();
        AuthorizationChannelInterceptor messages = new AuthorizationChannelInterceptor(messageAuthorizationManager);
        messages.setAuthorizationEventPublisher(new SpringAuthorizationEventPublisher(this.context));

        registration.interceptors(new SecurityContextChannelInterceptor(), authz, messages);
    }


}
