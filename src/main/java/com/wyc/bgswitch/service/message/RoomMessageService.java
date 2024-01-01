package com.wyc.bgswitch.service.message;

import com.wyc.bgswitch.entity.RoomInfoVO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author wyc
 */
@Service
public class RoomMessageService {
    private final SimpMessagingTemplate messaging;
    @Value("${prefix.ws.channels.room}")
    private String channelPrefixRoom;

    public RoomMessageService(SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    public void notifyUpdate(RoomInfoVO roomInfo) {
        roomInfo.getUserIds().forEach(u -> {
            messaging.convertAndSendToUser(u, String.format("%s/%s", channelPrefixRoom, roomInfo.getId()), roomInfo);
        });
    }
}
