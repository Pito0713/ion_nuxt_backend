package com.example.ion_nuxt_back.service;
import com.example.ion_nuxt_back.common.ApiResponse;
import com.example.ion_nuxt_back.dto.blogs.requset.PostBlogReqDTO;
import com.example.ion_nuxt_back.dto.blogs.response.GetBlogResDTO;
import com.example.ion_nuxt_back.dto.blogs.response.TagDTO;
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqDTO;
import com.example.ion_nuxt_back.model.Blog;

import com.example.ion_nuxt_back.model.Tags;
import com.example.ion_nuxt_back.repository.BlogRepository;
import com.example.ion_nuxt_back.repository.TagsRepository;
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
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
    @Autowired private TagsRepository tagsRepository;
    @Autowired private MongoTemplate mongoTemplate;

    // Post 新增 Blog 文章
    public ResponseEntity<ApiResponse<?>> postBlog(
            PostBlogReqDTO request,
            String userUUID
    ) {
        try {

            Blog blog = new Blog();
            blog.setTitle(request.getTitle());
            blog.setTextContent(request.getTextContent());
            blog.setUserUUID(userUUID);
            blog.setTagUUID(request.getTagUUID());
            blog.setCreateTime(new Date());
            blog.setUpdateTime(null);
            blog.setBlogCounts(0);

            // 使用 Jsoup 函式庫將 HTML 轉換為純文字
            String plainText = Jsoup.parse(request.getTextContent()).text();
            if (request.getTextContent() == null || request.getTextContent().isEmpty()) {
                blog.setPreviewText("");
            }

            // 截取前 100 個字元
            if (plainText.length() > 150) {
                blog.setPreviewText(plainText.substring(0, 150) + "...");
            } else {
                blog.setPreviewText(plainText);
            }

            blogRepository.save(blog);
//            // 取得生成的 ObjectId
//            String blogId = savedBlog.getId();
//            Optional<Tags> optionalTags = tagsRepository.findByUuid(request.getTagUUID());
//            // target Tags exist check
//            if (optionalTags.isPresent()) {
//                Tags tagsItem = optionalTags.get();
//
//                String id = tagsItem.getId();
//                Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
//                Tags.Blogs newBlog = new Tags.Blogs(blogId, blog.getTitle());
//                Update update = new Update()
//                        .push("blogs", newBlog);
//
//                mongoTemplate.updateFirst(query, update, Tags.class);
//            }

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
            // 分頁
            int skip = (page - 1) * pageSize;
            query.skip(skip).limit(pageSize);
            List<Blog> blogs = mongoTemplate.find(query, Blog.class);

            // 遍歷每個 blog
            for (Blog blog : blogs) {
                TagDTO blogTag = null;
                if (blog.getTagUUID() != null) {
                    Optional<Tags> optionalTags = tagsRepository.findByUuid(blog.getTagUUID());
                    blogTag = new TagDTO();
                    if (optionalTags.isPresent()) {
                        Tags tagsItem = optionalTags.get();
                        blogTag.setLabel(tagsItem.getLabel());
                        blogTag.setImgURL(tagsItem.getImgURL());
                    }
                }
                // 將 blogTag 物件設定到 blog 中
                blog.setTag(blogTag);
            }

            List<GetBlogResDTO> optionalBlog = blogs.stream()
                    .map(blog -> new GetBlogResDTO(
                            blog.getId(),
                            blog.getTitle(),
                            blog.getTextContent(),
                            blog.getUserUUID(),
                            (TagDTO) blog.getTag(),
                            blog.getPreviewText(),
                            blog.getCreateTime(),
                            blog.getUpdateTime(),
                            blog.getBlogCounts()
                    ))
                    .toList();

            // 建立一個新的 Query，只保留篩選條件，不要 skip/limit
            Query countQuery = Query.of(query).limit(-1).skip(-1);
            // 總筆數（符合條件的所有資料）
            long total = mongoTemplate.count(countQuery, Blog.class);
            return ResponseEntity.ok(ApiResponse.success(optionalBlog, (int) total));
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
            assert blog != null;
            Update update = new Update()
                    .set("blogCounts",blog.getBlogCounts() + 1);
            mongoTemplate.updateFirst(query, update, Blog.class);
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

            // 使用 Jsoup 函式庫將 HTML 轉換為純文字
            String plainText;
            // 截取前 100 個字元
            if (Jsoup.parse(request.getTextContent()).text().length() > 150) {
                plainText = Jsoup.parse(request.getTextContent()).text().substring(0, 150) + "...";
            } else {
                plainText =  Jsoup.parse(request.getTextContent()).text();
            }

            Update update = new Update()
                    .set("title", request.getTitle())
                    .set("textContent", request.getTextContent())
                    .set("previewText", plainText)
                    .set("tagUUID", request.getTagUUID())
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
