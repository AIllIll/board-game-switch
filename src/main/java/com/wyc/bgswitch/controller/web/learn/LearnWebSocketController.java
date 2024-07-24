package com.wyc.bgswitch.controller.web.learn;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wyc
 */
@ApiRestController
@RequestMapping("/learn/websocket")
public class LearnWebSocketController {

    private final SimpUserRegistry simpUserRegistry;

    public LearnWebSocketController(SimpUserRegistry simpUserRegistry) {
        this.simpUserRegistry = simpUserRegistry;
    }

    @GetMapping("/users")
    public String learnWebsocket() {
        return simpUserRegistry.getUsers().toString();
    }
}
