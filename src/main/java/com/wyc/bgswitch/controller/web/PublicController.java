package com.wyc.bgswitch.controller.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@ApiRestController
@RequestMapping("/public")
public class PublicController {
    @GetMapping("hello")
    public String hello() {
        return "hello";
    }
}
