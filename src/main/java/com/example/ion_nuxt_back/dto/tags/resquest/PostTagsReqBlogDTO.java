package com.example.ion_nuxt_back.dto.tags.resquest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class PostTagsReqBlogDTO {
    private String id;
    private String title;
}

