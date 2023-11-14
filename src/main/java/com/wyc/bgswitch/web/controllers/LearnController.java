package com.wyc.bgswitch.web.controllers;

import com.wyc.bgswitch.game.citadel.CitadelAction;
import com.wyc.bgswitch.game.citadel.CitadelEffect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learn")
public class LearnController {
    private SimpMessagingTemplate template;

    @Autowired
    public LearnController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("lllasfasfal %s!", name);
    }

    @GetMapping("/broadcast")
    public String broadcast(@RequestParam()String msg) {
        System.out.println(123);
        this.template.convertAndSend(
                "/topic/citadel",
                new CitadelEffect(null,"", msg)
        );
        this.template.convertAndSend(
                "/bgs/citadel",
                new CitadelEffect(null, "",msg)
        );
        return msg;
    }


}
