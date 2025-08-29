package com.example.ion_nuxt_back.dto.users.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子

public class userPasswordChangeReqDTO {
    private String newPassword;
    private String oldPassword;
    private String confirmPassword;
}