package com.wyc.bgswitch.config.websocket;

import com.wyc.bgswitch.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Component
public class HandshakeHandler extends DefaultHandshakeHandler {


    @Autowired
    private JwtService jwtUtils;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // 暂时从param里获取token
//        HttpHeaders headers = request.getHeaders();
//        String token = headers.get(headers.AUTHORIZATION).get(0);
//        if(token.startsWith("Bearer")) {
//            token = token.substring(7).trim();
//        }
//        Authentication user = jwtUtils.generateAuthFromToken(token);
//        final String name = user.getPrincipal()
//        return user.getPrincipal();
        return null;
        // todo: ws鉴权
    }
}
