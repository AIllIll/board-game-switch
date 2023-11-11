package com.wyc.bgswitch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BgswitchApplication {

	public static void main(String[] args) {
		SpringApplication.run(BgswitchApplication.class, args);
	}
	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hellssso %s!", name);
	}
	@GetMapping("/hello2")
	public String hello2(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello123 %s!", name);
	}
}
