package com.example.ion_nuxt_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有 API 路徑
                .allowedOrigins("http://localhost:3000") // 允許前端 Nuxt dev server
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 可接受的方法
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With") //  headers 允許
                .allowCredentials(true); // 若前端有帶 cookie 等資訊
    }
}

