package com.example.ion_nuxt_back.dto.blogs.response;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "blogs")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class GetBlogResDTO {
    @Id
    private String id;
    private String title;
    private String textContent;
    private String userUUID;
    private TagDTO tag;     // 單一標籤
    private String previewText;
    private Date createTime;
    private Date updateTime;
    private Integer blogCounts;
}


