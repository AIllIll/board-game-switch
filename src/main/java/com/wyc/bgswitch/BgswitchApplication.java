package com.wyc.bgswitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BgswitchApplication {
    public static void main(String[] args) {
        SpringApplication.run(BgswitchApplication.class, args);
    }
//	@RequestMapping("/")
//
//	protected String redirect()
//	{
//		return "redirect:http://localhost:8080/index.html";
//	}
}
