package com.yy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class StaticResourceConfig extends WebMvcConfigurerAdapter {

    /**
     * 给用户下载的文件等放在res/download文件夹下
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/download/**").addResourceLocations("file:res/download/");
        super.addResourceHandlers(registry);
    }

}