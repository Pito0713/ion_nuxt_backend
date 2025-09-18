package com.example.ion_nuxt_back.config;

import io.imagekit.sdk.ImageKit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageKitConfig {
    @Bean
    public ImageKit imageKit(
            @Value("${imagekit.publicKey}") String publicKey,
            @Value("${imagekit.privateKey}") String privateKey,
            @Value("${imagekit.urlEndpoint}") String urlEndpoint
    ) {
        ImageKit ik = ImageKit.getInstance();
        // 用完整類名避免與 Spring 的 @Configuration 混淆
        ik.setConfig(new io.imagekit.sdk.config.Configuration(publicKey, privateKey, urlEndpoint));
        return ik;
    }
}
