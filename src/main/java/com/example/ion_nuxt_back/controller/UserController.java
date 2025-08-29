package com.example.ion_nuxt_back.controller;
// DTO
import com.example.ion_nuxt_back.dto.users.request.LogInUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RefreshTokenReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RegisterUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.userPasswordChangeReqDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.ion_nuxt_back.common.ApiResponse;
import com.example.ion_nuxt_back.service.UserService;
import org.springframework.http.ResponseEntity;

@RestController // REST API
@RequestMapping("/users")  // base router
public class UserController {
    @Autowired private UserService userService;

    // POST 新增使用者
    @PostMapping
    public ResponseEntity<ApiResponse<?>> registerUserC(
            @RequestBody @Valid RegisterUserReqDTO request
    ) {
        return userService.registerUser(request);
    }

    // POST 登入
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> logInUserC(
            @RequestBody LogInUserReqDTO request
    ) {
        return userService.logInUser(request);
    }

    // GET 使用者info
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> getUserInfoC(
            @PathVariable String uuid
    ) {
        return userService.getUserInfo(uuid);
    }

    // POST 重新取得 token
    @PostMapping("/token/refresh")
    public ResponseEntity<ApiResponse<?>> refreshTokenC(
            @RequestBody RefreshTokenReqDTO request
    ) {
        return userService.refreshToken(request);
    }

    // POST 修改密碼
    @PostMapping("/userPassword")
    public ResponseEntity<ApiResponse<?>> userPasswordChangeC (
            @CookieValue(value = "userAccessToken", required = true) String userToken,
            @RequestBody userPasswordChangeReqDTO request
    ) {
        return  userService.userPasswordChange(userToken, request);
    }
}
