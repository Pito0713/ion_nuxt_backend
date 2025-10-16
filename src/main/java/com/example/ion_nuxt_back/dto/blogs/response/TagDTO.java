package com.example.ion_nuxt_back.dto.blogs.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class TagDTO {
    private String label;
    private String imgURL;
}
