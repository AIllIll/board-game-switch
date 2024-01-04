package com.wyc.bgswitch.service.message;

import com.wyc.bgswitch.redis.entity.game.citadel.CitadelGame;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyc
 */
@Service
public class GameMessageService {
    private final SimpMessagingTemplate messaging;
    @Value("${prefix.ws.channels.game.citadel}")
    private String channelPrefixGameCitadel;

    public GameMessageService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void notifyUpdate(List<String> userIds, CitadelGame game) {
        userIds.forEach(u -> {
            messaging.convertAndSendToUser(u, String.format("%s/%s", channelPrefixGameCitadel, game.getId()), game.masked(u));
        });
    }
}