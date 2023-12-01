package com.wyc.bgswitch.config.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {
    private static final String publicPath = "/bgs-front/";

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(ApiRestController.class));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));


        registry.addResourceHandler(publicPath + "**")
                .addResourceLocations("classpath:/web-build/")
//                .addResourceLocations("file:///./home/wyc/Desktop/develop/bgswitch-frontend/dist/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:/bgs");
        registry.addViewController("/static").setViewName("forward:/static/index.html");
        registry.addViewController("/bgs/**").setViewName("forward:" + publicPath + "index.html");
    }
}