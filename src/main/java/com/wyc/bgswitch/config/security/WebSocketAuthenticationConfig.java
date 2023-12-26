package com.wyc.bgswitch.config.security;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

import com.wyc.bgswitch.service.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;


/**
 * @author wyc
 */
@Configuration
@Order(HIGHEST_PRECEDENCE + 99)
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtUtils;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                System.out.println(message);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    try {
                        System.out.println("IS CONNECT");
                        String token = message.getHeaders().get("nativeHeaders", LinkedMultiValueMap.class).get("JWT").get(0).toString();
                        // Authentication user = new JwtBearerTokenAuthenticationConverter().convert(jwt);
                        // 上面这行代码会将Authorities设置为SCOPE_ROLE_USER，而我希望的是ROLE_USER，因此我自己写了一个
                        Authentication user = jwtUtils.generateAuthFromToken(token);
                        accessor.setUser(user);
                        System.out.println("AND USER IS SET");
                    } catch (Exception e) {
                        System.out.println("invalid jwt");
//                        throw e;
                    }
                }
                return message;
            }
        });

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }


}
