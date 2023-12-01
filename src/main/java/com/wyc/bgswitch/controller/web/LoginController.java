package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@ApiRestController
public class LoginController {
    @CrossOrigin
    @PostMapping("/login")
    public String login() {
        return "666";
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
