package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.service.JwtService;
import com.wyc.bgswitch.utils.debug.Debug;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author wyc
 */
@ApiRestController
public class LoginController {
    private final Logger logger = LogManager.getLogger(LoginController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtUtils;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, JwtService jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @CrossOrigin
    @PostMapping("/login")
    @Debug
    public String token(
            Authentication authentication,
            @RequestBody(required = false) LoginRequestParams loginRequestParams
    ) {
        if (authentication == null) {
            Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                    loginRequestParams.username,
                    loginRequestParams.password
            );
            authentication = this.authenticationManager.authenticate(authenticationRequest);
        }
        return jwtUtils.generateTokenFromAuth(authentication);
    }

    @CrossOrigin
    @PostMapping("/refresh")
    @Debug
    public String refreshToken(@RequestHeader("Authorization") String token) {
        System.out.println(token);
        return this.jwtUtils.refreshToken(token.replaceFirst("Bearer ", ""));
    }

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken token) {
        return token;
    }

    public record LoginRequestParams(String username, String password) {
    }
}
