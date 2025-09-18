package com.example.ion_nuxt_back.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "blogs")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class Blog {
    @Id
    private String id;
    private String title;
    private String textContent;
    private String userUUID;
    private String tag;
    private String previewText;
    private Date createTime;
    private Date updateTime;
    private Integer blogCounts;
}
