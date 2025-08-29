package com.example.ion_nuxt_back.common;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 套用到所有回傳物件
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body, MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response
    ) {
        // 已是 ApiResponse 就不包
        if (body instanceof ApiResponse) {
            return body;
        }

        // 嘗試從 response 中取得 HTTP 狀態碼
        int status = 200; // 預設成功
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
        }

        if (status >= 200 && status < 300) {
            return ApiResponse.success(body);
        } else {
            return ApiResponse.error(body);
        }
    }
}