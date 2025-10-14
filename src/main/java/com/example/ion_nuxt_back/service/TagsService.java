package com.example.ion_nuxt_back.service;
import com.example.ion_nuxt_back.common.ApiResponse;

import com.example.ion_nuxt_back.dto.tags.response.PostTagsResDTO;
import com.example.ion_nuxt_back.dto.tags.response.PostEditTagsResDTO;
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqBlogDTO;
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqDTO;
import com.example.ion_nuxt_back.model.Blog;
import com.example.ion_nuxt_back.model.Tags;
import com.example.ion_nuxt_back.repository.BlogRepository;
import com.example.ion_nuxt_back.repository.TagsRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TagsService {
    @Autowired private BlogRepository blogRepository;
    @Autowired private TagsRepository tagsRepository;
    @Autowired private MongoTemplate mongoTemplate;
    public ResponseEntity<ApiResponse<?>> postTags(
            PostTagsResDTO request
    ) {
        try {
            // param
            String label = request.getLabel();
            String imgUrl = request.getImgURL();

            Tags tags = new Tags();
            tags.setLabel(label);
            tags.setImgURL(imgUrl);
            tags.setUuid(UUID.randomUUID().toString());
            tags.setTagCounts(0);
            tags.setBlogs(new ArrayList<>());
            tags.setCreateTime(new Date());
            tags.setUpdateTime(new Date());
            tagsRepository.save(tags);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> getTags() {
        try {
            List<Tags> tags = tagsRepository.findAll();

            List<PostTagsReqDTO> optionalTag = tags.stream()
                    .map(tag -> {
                        List<Blog> optionalBlogs = blogRepository.findByTagUUID(tag.getUuid());
                        List<PostTagsReqBlogDTO> blogs = optionalBlogs.stream()
                                .map(blog -> new PostTagsReqBlogDTO(
                                        blog.getId(),
                                        blog.getTitle()
                                ))
                                .toList();
                        //  建立 PostTagsReqDTO，傳入轉換好的 DTO 列表
                        return new PostTagsReqDTO(
                                tag.getLabel(),
                                tag.getUuid(),
                                tag.getTagCounts(),
                                tag.getImgURL(),
                                tag.getCreateTime(),
                                tag.getUpdateTime(),
                                blogs
                        );
                    })
                    .toList();

            return ResponseEntity.ok(ApiResponse.success(optionalTag));
        } catch (Exception e) {
        // 回傳錯誤 response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> editTag (
            PostEditTagsResDTO request,
            String uuid
    ) {
        try {
            Optional<Tags> optionalTags = tagsRepository.findByUuid(uuid);

            // target Tags exist check
            if (optionalTags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1007));
            }
            Tags tagsItem = optionalTags.get();

            String id = tagsItem.getId();
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            System.out.println( request.getImgURL());
            Update update = new Update()
                    .set("label", request.getLabel())
                    .set("imgURL", request.getImgURL());
            mongoTemplate.updateFirst(query, update, Tags.class);

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> deleteTag (
            String uuid
    ) {
        try {
            Optional<Tags> optionalTags = tagsRepository.findByUuid(uuid);

            // target Tags exist check
            if (optionalTags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1007));
            }

            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

}
