package com.example.ion_nuxt_back.config;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/* -------------------------------------------------------------------------------------------
    RestBasicAuthEntryPoint 繼承 BasicAuthenticationEntryPoint 並修改統一回覆 401 驗證失敗

    BasicAuthenticationEntryPoint 默認 Spring Security Http Basic 認證失敗 與 認證空白處理

    Spring Security 調用 commence 進行 HttpServlet 驗證失敗狀態重寫並返回內容
    當 Bean 內容設置好返回屬性, 調用 afterPropertiesSet 讓由父組件進行 類別初始檢查
---------------------------------------------------------------------------------------------*/

public class AuthEntryPointHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 UNAUTHORIZED
        response.setContentType("application/json;charset=UTF-8");
        String body = new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(
                        // 調用 ApiResponse 統一 api 格式
                        com.example.ion_nuxt_back.common.ApiResponse.error("unauthorized", 1010)
                );
        response.getWriter().write(body);
    }

}
