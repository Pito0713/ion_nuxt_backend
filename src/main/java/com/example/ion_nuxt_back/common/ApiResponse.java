package com.example.ion_nuxt_back.common;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final int status;
    private final T data;
    private final Integer totalCount;
    private final Integer errorStatusCode;

    public ApiResponse(int status, T data, Integer totalCount, Integer errorStatusCode) {
        this.status = status;
        this.data = data;
        this.totalCount = totalCount;
        this.errorStatusCode = errorStatusCode;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(1, data, null , null);
    }
    public static <T> ApiResponse<T> success(T data, int totalCount) {
        return new ApiResponse<>(1, data, totalCount, null);
    }
    public static <T> ApiResponse<T> error(T data) {
        return new ApiResponse<>(0, data,null, null);
    }
    public static <T> ApiResponse<T> error(T data, int errorStatusCode) {
        return new ApiResponse<>(0, data,null, errorStatusCode);
    }
}
