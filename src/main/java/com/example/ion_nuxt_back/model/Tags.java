package com.example.ion_nuxt_back.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "tags")
//  Lombok
@Data //  auto getter, setter, toString, equals, hashCode
@NoArgsConstructor // 無參數建構子
@AllArgsConstructor // 全參數建構子
public class Tags {
    @Id
    private String id;
    private String uuid;
    private String label;
    private Date createTime;
    private Date updateTime;
    private Integer tagCounts;
    private String imgURL;
    private List<Blogs> blogs;
    // inner class
    //  Lombok
    @Data //  auto getter, setter, toString, equals, hashCode
    @NoArgsConstructor // 無參數建構子
    @AllArgsConstructor // 全參數建構子
    public static class Blogs {
        private String id;
        private String title;
    }
}
