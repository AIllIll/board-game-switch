package com.wyc.bgswitch.controller.web;

import com.alibaba.fastjson2.JSON;
import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.service.AuthService;
import com.wyc.bgswitch.service.JwtService;
import com.wyc.bgswitch.utils.debug.Debug;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wyc
 */
@ApiRestController
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtUtils;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtUtils) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    @Debug
    public void register(
            @RequestBody LoginRequestParams loginRequestParams
    ) {
        authService.register(loginRequestParams.username(), loginRequestParams.password());
    }

    /**
     * 登录与续签
     *
     * @param loginRequestParams 如果是续签，此参数设置为null
     * @param authentication
     * @return token 形式是：“Bearer xxx”
     */
    @PostMapping("/login")
    @Debug
    public String token(
            @RequestBody(required = false) LoginRequestParams loginRequestParams,
            Authentication authentication
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

    /**
     * 微信小程序获取openId并登录
     *
     * @param code           登录凭证，用于后端向微信服务器换取openid
     * @param authentication
     * @return
     */
    @Debug
    @PostMapping("/login/weapp")
    public String weappLogin(
            @RequestBody(required = false) String code,
            Authentication authentication
    ) {
        if (authentication == null) {
            String openId;
            if (code.equals("123456")) {
                //用于测试
                openId = code;
            } else {
                // todo: 收录到properties
                String appid = "wx714795678d04b0c4";
                String secret = "fdb7f1a710648d1275b9cb3d76a53978";
                String url = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code".formatted(appid, secret, code);
                RestTemplate restTemplate = new RestTemplate();
                String response = restTemplate.getForObject(url, String.class);
                Map<String, String> map = JSON.parseObject(response, HashMap.class);
                openId = map.get("openid");
            }
            // 如果没有注册，先注册
            if (!authService.userExists("weapp-" + openId)) {
                authService.register("weapp-" + openId, "meiyoumima");
            }
            // 登录
            Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
                    "weapp-" + openId,
                    "meiyoumima" // 默认密码，微信只要能拿到openId就是本人，不会有错的
            );
            authentication = this.authenticationManager.authenticate(authenticationRequest);
        }
        return jwtUtils.generateTokenFromAuth(authentication); // 返回token就行
    }

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
