package com.wyc.bgswitch.config.web;

import com.wyc.bgswitch.config.web.annotation.ApiRestController;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${prefix.api}")
    private String apiPrefix;
    @Value("${prefix.resources.bgs.web}")
    private String bgsWebPath;
    @Value("${prefix.resources.bgs.static}")
    private String bgsResourcesPath;
    @Value("${prefix.resources.static}")
    private String staticResourcesPath;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(apiPrefix, HandlerTypePredicate.forAnnotation(ApiRestController.class));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler(staticResourcesPath + "/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));


        registry.addResourceHandler(bgsResourcesPath + "/**", "/**")
                .addResourceLocations("classpath:/dist/")
//                .addResourceLocations("file:///./home/wyc/Desktop/develop/bgswitch-frontend/dist/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)));

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("redirect:" + bgsWebPath); // 将根路径映射到bgs网站路径
        registry.addViewController(bgsWebPath + "/**").setViewName("forward:/front/index.html"); // 前端应用前缀下的路径，交给前端来路由
        registry.addViewController("/static").setViewName("forward:/static/index.html"); // 简易同源测试网站
    }
}