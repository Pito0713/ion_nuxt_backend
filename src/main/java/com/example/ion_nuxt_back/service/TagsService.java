package com.example.ion_nuxt_back.service;
// common
import com.example.ion_nuxt_back.common.ApiResponse;
// DTO.response
import com.example.ion_nuxt_back.dto.tags.response.PostTagsResDTO;
import com.example.ion_nuxt_back.dto.tags.response.PostEditTagsResDTO;
// DTO.resquest
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqBlogDTO;
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqDTO;
// model
import com.example.ion_nuxt_back.model.Blog;
import com.example.ion_nuxt_back.model.Tags;
// model repository
import com.example.ion_nuxt_back.repository.BlogRepository;
import com.example.ion_nuxt_back.repository.TagsRepository;
// springframework
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
// util
import org.bson.types.ObjectId;
import java.util.*;

@Service
public class TagsService {
    @Autowired private BlogRepository blogRepository;
    @Autowired private TagsRepository tagsRepository;
    @Autowired private MongoTemplate mongoTemplate;
    // Post 新增 tags 標籤
    public ResponseEntity<ApiResponse<?>> postTags(
            PostTagsResDTO request
    ) {
        try {
            // param
            String label = request.getLabel();
            String imgUrl = request.getImgURL();
            // conditional: 判斷標籤值 是否空值
            if ((label == null || label.trim().isEmpty())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("label_is_Empty", 1008));
            }

            Tags tags = new Tags();
            tags.setLabel(label);
            tags.setImgURL(imgUrl);
            tags.setUuid(UUID.randomUUID().toString());
            tags.setTagCounts(0);
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

    // Get 取得 tags 標籤;
    public ResponseEntity<ApiResponse<?>> getTags() {
        try {
            // 取得所有 tag
            List<Tags> tags = tagsRepository.findAll();

            // Map 每筆 tag 的 UUID
            // 在 blogRepository 裡塞選 blog 有對應的 tag UUID
            // 並回傳 blog: {title, objectId}
            List<PostTagsReqDTO> optionalTag = tags.stream()
                    .map(tag -> {
                        List<Blog> optionalBlogs = blogRepository.findByTagUUID(tag.getUuid());
                        List<PostTagsReqBlogDTO> blogs = optionalBlogs.stream()
                                .map(blog -> new PostTagsReqBlogDTO(
                                        blog.getId(),
                                        blog.getTitle()
                                ))
                                .toList();
                        //  建立 PostTagsReqDTO，傳入轉換好的 blogs 列表
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

    // Post 編輯 Tags 標籤;
    public ResponseEntity<ApiResponse<?>> editTag (
            PostEditTagsResDTO request,
            String uuid
    ) {
        try {
            // param
            String label = request.getLabel();
            String imgUrl = request.getImgURL();
            // 取得該 UUID 資料
            Optional<Tags> optionalTags = tagsRepository.findByUuid(uuid);

            // conditional: 判斷標籤值 是否空值
            if (optionalTags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("account_error", 1009));
            }

            // update
            Tags tagsItem = optionalTags.get();
            String id = tagsItem.getId();
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            Update update = new Update()
                    .set("label", label)
                    .set("imgURL", imgUrl);
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
            if (( uuid == null || uuid.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            // 先檢查是否存在（MongoRepository.deleteById 不會丟 not found）
            // 取得該 UUID 資料
            Optional<Tags> optionalTags = tagsRepository.findByUuid(uuid);

            // conditional: 判斷標籤值 是否空值
            if (optionalTags.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("tag_not_found", 1009));
            }

            // update
            Tags tagsItem = optionalTags.get();

            // 3) 先清 Blog 參照（避免刪了 tag 後 Blog 指到不存在的 uuid）
            Query queryUUID = Query.query(Criteria.where("tagUUID").is(uuid));
            Update UpdateNull = new Update().unset("tagUUID"); // 也可改成 .set("tagUUID", null)
            UpdateResult ur = mongoTemplate.updateMulti(queryUUID, UpdateNull, Blog.class);

            String id = tagsItem.getId();

            tagsRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

}
