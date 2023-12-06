package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.utils.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@ApiRestController
public class LoginController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @CrossOrigin
    @PostMapping("/login")
    public String token(
            Authentication authentication,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password
    ) {
        if (authentication == null) {
            Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            authentication = this.authenticationManager.authenticate(authenticationRequest);
        }
        return jwtUtils.generateTokenFromAuth(authentication);
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }
}
