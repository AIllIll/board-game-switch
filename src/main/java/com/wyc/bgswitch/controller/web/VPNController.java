package com.wyc.bgswitch.controller.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.util.IOUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@RestController()
@RequestMapping("/VPN")
public class VPNController {

    @Value("${vpn.config}")
    private Resource awsConfigJSON;

    private String ip = "13.230.16.62";

    public VPNController() {
        System.out.printf("Vpn ip: %s%n", this.ip);
    }

    @GetMapping("/subscription")
    public String subscription() throws IOException {
        String json = IOUtils.readInputStreamToString(awsConfigJSON.getInputStream());
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("add", this.ip);
        String vmessLink = "vmess://" + Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(vmessLink.getBytes(StandardCharsets.UTF_8));
    }

    @PostMapping("/update")
    public void updateIp(@RequestParam(value = "ip") String ip) {
        System.out.println("new vpn ip: " + ip);
        this.ip = ip;
    }
}
