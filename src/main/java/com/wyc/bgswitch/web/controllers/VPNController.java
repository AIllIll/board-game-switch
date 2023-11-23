package com.wyc.bgswitch.web.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.util.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.Base64;


@RestController()
@RequestMapping("/VPN")
public class VPNController {

    @Value("${vpn.config}")
    private Resource awsConfigJSON;

    private String ip = "176.34.20.197";

    public VPNController(){
        System.out.println(String.format("Vpn ip: %s", this.ip));
    }

    @GetMapping("/subscription")
    public String subscription() throws IOException {
        String json = new String(IOUtils.readInputStreamToString(awsConfigJSON.getInputStream()));
        JSONObject jsonObject = JSON.parseObject(json);
        jsonObject.put("add", this.ip);
        String vmessLink = "vmess://"+Base64.getEncoder().encodeToString(jsonObject.toJSONString().getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(vmessLink.getBytes("utf-8"));
    }

    @PostMapping("/update")
    public void updateIp(@RequestParam(value = "ip") String ip) {
        System.out.println("new vpn ip: " + ip);
        this.ip = ip;
    }
}
