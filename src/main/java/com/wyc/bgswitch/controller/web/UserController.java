package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@ApiRestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("")
    public UserInfo getUser(Principal principal) {
        return new UserInfo(
                principal.getName(),
                principal.getName(),
                "https://zh.wikipedia.org/static/images/icons/wikipedia.png"
        );
    }

    public record UserInfo(String id, String name, String avatar) {
    }

}
