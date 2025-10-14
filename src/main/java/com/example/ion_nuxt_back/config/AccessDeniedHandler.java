package com.example.ion_nuxt_back.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

/* -------------------------------------------------------------------------------------------
    RestBasicAuthEntryPoint 繼承 BasicAuthenticationEntryPoint 並修改統一回覆 401 驗證失敗

    BasicAuthenticationEntryPoint 默認 Spring Security Http Basic 認證失敗 與 認證空白處理

    Spring Security 調用 commence 進行 HttpServlet 驗證失敗狀態重寫並返回內容
    當 Bean 內容設置好返回屬性, 調用 afterPropertiesSet 讓由父組件進行 類別初始檢查
---------------------------------------------------------------------------------------------*/

public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403 UNAUTHORIZED
        response.setContentType("application/json;charset=UTF-8");
        String body = new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(
                        // 調用 ApiResponse 統一 api 格式
                        com.example.ion_nuxt_back.common.ApiResponse.error("unauthorized", 1010)
                );
        response.getWriter().write(body);
    }
}
