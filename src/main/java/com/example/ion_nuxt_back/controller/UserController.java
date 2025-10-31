package com.example.ion_nuxt_back.controller;
// DTO
import com.example.ion_nuxt_back.dto.users.request.LogInUserReqDTO;
import com.example.ion_nuxt_back.dto.users.request.RegisterUserReqDTO;
// springframework
import com.example.ion_nuxt_back.dto.users.request.editUserReqDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
// common
import com.example.ion_nuxt_back.common.ApiResponse;
// service
import com.example.ion_nuxt_back.service.UserService;
// util
import jakarta.validation.Valid;

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
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserInfoC(
            @CookieValue(value = "userUUID", required = true) String uuid
    ) {
        return userService.getUserInfo(uuid);
    }

    // POST 修改會員資料
    @PostMapping("/edit")
    public ResponseEntity<ApiResponse<?>> editUserC(
            @RequestBody editUserReqDTO request,
            @CookieValue(value = "userUUID", required = true) String userUUID
    ) {
        return userService.editUser(request, userUUID);
    }
}
