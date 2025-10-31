package com.example.ion_nuxt_back.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "users")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class User {
    @Id
    private String id;
    private String account;
    private String password;
    private String accessToken;
    private String refreshToken;
    private Date createTime;
    private String uuid;
    private String nick;
    private String infoImg;
}
