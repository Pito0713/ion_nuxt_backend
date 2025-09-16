package com.example.ion_nuxt_back.controller;
import io.imagekit.sdk.ImageKit;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/imagekit")
@RequiredArgsConstructor
public class ImageKitAuthController {
    private final ImageKit imageKit;

    @GetMapping("/auth")
    public Map<String, String> getAuth() {
        String token = UUID.randomUUID().toString();
        long expire = Instant.now().getEpochSecond() + 5 * 60; // 秒
        Map<String, String> auth = imageKit.getAuthenticationParameters(token, expire);
        System.out.println("IK auth expire(raw)=" + auth.get("expire")); // 應該是 10 位
        return auth;
    }
}
