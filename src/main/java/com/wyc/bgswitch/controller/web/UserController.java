package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;
import com.wyc.bgswitch.entity.UserInfo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wyc
 */
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

    @GetMapping("/list")
    public List<UserInfo> getUserList(@RequestParam String[] userIds) {
        return Arrays.stream(userIds).map(u -> new UserInfo(
                u,
                u,
                "https://zh.wikipedia.org/static/images/icons/wikipedia.png"
        )).collect(Collectors.toList());
    }

}
