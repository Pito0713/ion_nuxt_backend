// SecurityConfig.java
package com.example.ion_nuxt_back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
/* -------------------------------------------------------------------------------------------
    流程
        CORS 調用 自訂義 corsConfigurationSource 進行 URL config 的分配與配置
        CSRF 關閉使用 JWT 進行使用者憑證
        Session 關閉使用 JWT 進行使用者憑證
        URL requestMatchers HttpMethod 判斷 URL 與 Method 授權
        Filter .addFilterBefore 注入 自訂 jwtAuthenticationFilter
        Exception 例外處理 注入 自訂 RestBasicAuthEntryPoint
---------------------------------------------------------------------------------------------*/

@Configuration  // 設定檔
// @EnableWebSecurity  // 啟用 Spring Security 的 Web 安全支援  Spring Boot 3.0+ 後可不加 要有 spring-boot-starter-security
public class SecurityConfig {
    // 注入 JwtAuthenticationFilter Bean
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain filterChain (
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
                // 啟用 cors 並使用下面自訂是的 Bean corsConfigurationSource
                .cors(cors -> cors.configurationSource(corsConfigurationSource())
                )
                // 關閉 CSRF, 目前使用 JWT
                .csrf(AbstractHttpConfigurer::disable)
                // 關閉 session, 目前使用 JWT
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // URL 授權規則
//                .authorizeHttpRequests(auth ->
//                        auth
//                            .requestMatchers(HttpMethod.POST, "/users").permitAll() // 註冊 無需認證
//                            .requestMatchers(HttpMethod.POST, "/users/login").permitAll() // 登入 無需認證
//                            .requestMatchers(HttpMethod.POST, "/users/token/refresh").permitAll() // 登入 無需認證
//                            .anyRequest().authenticated() // 其它請求都需認證
//                )
                // 插入自訂的 JWT Filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 例外處理
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new AuthEntryPointHandler()) // 401  未認證或認證失敗
                        .accessDeniedHandler(new AccessDeniedHandler()) // 403 已認證但權限不足
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // 宣告 config 物件
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));  // 前端開發地址
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS")); // 允許的 Http Method
        config.setAllowedHeaders(List.of("*")); // 允許 header
        config.setAllowCredentials(true);  // 允許憑證 cookie

        // 宣告 URL 配置
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 把 config 映射到 URL 配置 並且 ** 全部
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthEntryPointHandler restBasicAuthEntryPoint() {
        return new AuthEntryPointHandler();
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new AccessDeniedHandler();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}