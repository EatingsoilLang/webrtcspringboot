package com.citydo.webrtcspringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Package com.citydo.webrtcspringboot.config
 * @ClassName WebMvcConfig
 * @Description TODO
 * @Author LangShengJie
 * @Date Created in 2020/12/14 15:08
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:D:/manager/");
    }
}
