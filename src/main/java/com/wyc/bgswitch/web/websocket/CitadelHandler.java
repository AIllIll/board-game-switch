package com.wyc.bgswitch.web.websocket;

import com.wyc.bgswitch.game.citadel.CitadelAction;
import com.wyc.bgswitch.game.citadel.CitadelEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;

@Controller
public class CitadelHandler {
    private SimpMessagingTemplate template;
    @Autowired
    public CitadelHandler(SimpMessagingTemplate template) {
        this.template = template;
    }
    @MessageMapping("/citadel")
    @SendTo("/topic/citadel")
    public CitadelEffect actionToEffect(CitadelAction action, Principal principal) {
        System.out.println(action);
        System.out.println(principal);
        this.template.convertAndSend(
                "/topic/citadel",
                new CitadelEffect(null, action.getAction())
        );
        this.template.convertAndSend(
                "/bgs/citadel",
                new CitadelEffect(null, action.getAction())
        );

        this.template.convertAndSendToUser(",",
                "/bgs/citadel",
                new CitadelEffect(null, action.getAction())
        );
        return new CitadelEffect(action.getGameId(),"test666");
    }

    @MessageMapping("/citadel2")
    public CitadelEffect directlyReturn(CitadelAction action) {
        System.out.println(12334);
        return new CitadelEffect(action.getGameId(),action.getAction());
    }

    @MessageMapping("/citadel3")
    @SendToUser("/citadel")
    public CitadelEffect handleCitadel3(CitadelAction action) {
        System.out.println(654);
        return new CitadelEffect(action.getGameId(),action.getAction());
    }

}

