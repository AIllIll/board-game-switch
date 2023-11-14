package com.wyc.bgswitch.web.websocket;

import com.wyc.bgswitch.game.citadel.CitadelAction;
import com.wyc.bgswitch.game.citadel.CitadelEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@MessageMapping("/citadel")
public class CitadelWsController {
    private SimpMessagingTemplate template;
    @Autowired
    public CitadelWsController(SimpMessagingTemplate template) {
        this.template = template;
    }





    @MessageMapping("/citadel")
    @SendTo("/topic/citadel")
    public CitadelEffect actionToEffect(CitadelAction action, Principal principal) {
        System.out.println(action);
        System.out.println(principal);
        this.template.convertAndSend(
                "/topic/citadel",
                new CitadelEffect(null, "","template 返回给/topic/citadel")
        );
        this.template.convertAndSend(
                "/bgs/citadel",
                new CitadelEffect(null, "","template 返回给/bgs/citadel")
        );

        this.template.convertAndSendToUser(principal.getName(),
                "/topic/citadel",
                new CitadelEffect(null, "","template 返回给/user/queue/citadel")
        );
        return new CitadelEffect(action.getGameId(),"","@SendTo 返回给/topic/citadel");
    }
    @MessageMapping("/citadel4")
    @SendToUser(value = "/citadel", broadcast = false)
    public CitadelEffect handleCitadel4(CitadelAction action, Principal principal) {
        System.out.println(""+654+principal.getName());
        return new CitadelEffect(action.getGameId(), "","@SendToUser返回给/user/citadel");
    }

}

