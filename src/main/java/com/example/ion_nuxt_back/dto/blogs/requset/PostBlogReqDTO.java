package com.example.ion_nuxt_back.dto.blogs.requset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "blogs")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子

public class PostBlogReqDTO {
    private String title;
    private String textContent;
    private List<Tags> tags;
    private String userUUID;


    // inner class
    //  Lombok
    @Data //  auto getter, setter, toString, equals, hashCode
    @NoArgsConstructor // 無參數建構子
    @AllArgsConstructor // 全參數建構子
    public static class Tags {
        private String uuid;
        private String label;
    }
}
