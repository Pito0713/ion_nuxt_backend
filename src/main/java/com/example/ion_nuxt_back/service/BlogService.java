package com.example.ion_nuxt_back.service;
import com.example.ion_nuxt_back.common.ApiResponse;
import com.example.ion_nuxt_back.dto.blogs.requset.PostBlogReqDTO;
import com.example.ion_nuxt_back.model.Blog;

import com.example.ion_nuxt_back.repository.BlogRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    @Autowired private BlogRepository blogRepository;
    @Autowired private MongoTemplate mongoTemplate;
    public ResponseEntity<ApiResponse<?>> postBlog(
            PostBlogReqDTO request,
            String userUUID
    ) {
        try {
            Blog blog = new Blog();
            blog.setTitle(request.getTitle());
            blog.setTextContent(request.getTextContent());
            blog.setUserUUID(userUUID);
            // blog.setTags(request.getTags());
            blog.setCreateTime(new Date());
            blog.setUpdateTime(null);
            blog.setBlogCounts(0);

            blogRepository.save(blog);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> getBlog(
            Integer page,
            Integer pageSize
    ) {
        try {
            Query query = new Query();
//            if (tags != null && !tags.isEmpty() && !"0".equals(tags)) {
//                // 依你的實際結構二擇一：
//                // 若 tags 儲存為字串陣列：query.addCriteria(Criteria.where("tags").is(tags) 或 in(tagsList));
//                // 若 tags 為物件陣列（含 uuid）：用下面這行
//                query.addCriteria(Criteria.where("tags.uuid").is(tags));
//            }


            // 分頁
            int skip = (page - 1) * pageSize;
            query.skip(skip).limit(pageSize);
            List<Blog> blogs = mongoTemplate.find(query, Blog.class);
            // 建立一個新的 Query，只保留篩選條件，不要 skip/limit
            Query countQuery = Query.of(query).limit(-1).skip(-1);
            // 總筆數（符合條件的所有資料）
            long total = mongoTemplate.count(countQuery, Blog.class);
            return ResponseEntity.ok(ApiResponse.success(blogs, (int) total));
        } catch (Exception e) {
        // 回傳錯誤 response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("server_error", 1003));
        }
    }

    // Get 取得 Blog 文章
    public ResponseEntity<ApiResponse<?>> getSingleBlog (
            String id
    ) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            Blog blog = mongoTemplate.findOne(query, Blog.class); // 只會回傳單筆
            return ResponseEntity.ok(ApiResponse.success(blog));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> editBlog (
            PostBlogReqDTO request,
            String id
    ) {
        try {
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            Update update = new Update()
                    .set("title", request.getTitle())
                    .set("textContent", request.getTextContent())
                    .set("tags", request.getTags())
                    .set("updateTime", new Date());
            mongoTemplate.updateFirst(query, update, Blog.class);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> deleteBlog (
            String id
    ) {
        try {
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

}
