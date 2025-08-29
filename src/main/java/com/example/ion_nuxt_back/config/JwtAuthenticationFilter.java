// JwtAuthenticationFilter.java
package com.example.ion_nuxt_back.config;

import com.example.ion_nuxt_back.common.JwtSecret;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/* -------------------------------------------------------------------------------------------
    JwtAuthenticationFilter 繼承 OncePerRequestFilter
    並覆寫 doFilterInternal 做為 JWT 驗證處理

    OncePerRequestFilter 為 Spring Web 當請求發生時只執行一次 doFilterInternal

    取得 request header Auth 表頭, 並進行 Bearer token 拆分 並 JwtSecret 解析 token 取值,
    調用 Spring security .userDetails 建立一個 user builder 詳情物件
    然後再調用 UsernamePasswordAuthenticationToken 進行包裝 user 驗證 與 權限
    最後再 存入 SecurityContext
---------------------------------------------------------------------------------------------*/

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // Filter 會自動執行 doFilterInternal 過濾方法
    // 其參數包含請求（HttpServletRequest）、回應（HttpServletResponse）與過濾鏈（FilterChain）。
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)  // 過濾器鏈
            throws ServletException, IOException { // 例外處理
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);  // 取得 header Auth

        // 判斷 header Auth 值 與 拆分資料
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim(); // 擷取 7 開始 移除字串前後空白字元
            try {
                Claims claims = JwtSecret.parseToken(token); // 解析 token
                String username = claims.getSubject(); // 提值

                // 建立一個 Spring Security user builder 詳情物件
                UserDetails userDetails = User.withUsername(username) //
                        .password("") // 不需密碼
                        .authorities(Collections.emptyList()) // 暫無權限 使用空array
                        .build(); // 組裝
                // 建立 Spring Security  預設 UsernamePasswordAuthenticationToken 物件
                // 後續放到 SecurityContext
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, // user 詳情物件
                        null, // 暫無驗證 credentials
                        userDetails.getAuthorities() // 使用者 權限列表
                );
                // 存入 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ex) {
                // 例外 清除
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}