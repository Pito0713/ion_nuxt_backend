package com.example.ion_nuxt_back.dto.tags.resquest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "tags")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class PostTagsReqDTO {
    private String label;
    private String uuid;
    private Integer tagCount;
    private String imgURL;
    private Date createTime;
    private Date updateTime;
    private List<PostTagsReqBlogDTO> blogs;
}
