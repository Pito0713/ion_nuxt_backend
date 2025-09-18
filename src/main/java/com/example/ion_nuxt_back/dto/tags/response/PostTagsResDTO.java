package com.example.ion_nuxt_back.dto.tags.response;

import com.example.ion_nuxt_back.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "tags")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class PostTagsResDTO {
    private String label;
    private String uuid;
    private Integer tagCount;
    private List<Tags.Blogs> blogs;
    // inner class
    //  Lombok
    @Data //  auto getter, setter, toString, equals, hashCode
    @NoArgsConstructor // 無參數建構子
    @AllArgsConstructor // 全參數建構子
    public static class Blogs {
        @Id
        private String id;
        private String title;
    }
}
