package com.example.ion_nuxt_back.service;
import com.example.ion_nuxt_back.common.ApiResponse;

import com.example.ion_nuxt_back.dto.tags.response.PostTagsResDTO;
import com.example.ion_nuxt_back.dto.tags.resquest.PostTagsReqDTO;
import com.example.ion_nuxt_back.model.Tags;
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
    @Autowired private TagsRepository tagsRepository;
    @Autowired private MongoTemplate mongoTemplate;
    public ResponseEntity<ApiResponse<?>> postTags(
            PostTagsReqDTO request
    ) {
        try {
            // param
            String text = request.getLabel();

            Tags tags = new Tags();
            tags.setLabel(text);
            tags.setUuid(UUID.randomUUID().toString());
            tags.setTagCounts(0);
            tags.setBlogs(new ArrayList<>());
            tags.setCreateTime(new Date());
            tags.setUpdateTime(new Date());
            tags.setImgURL("");

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
            List<PostTagsResDTO> optionalTag = tags.stream()
                    .map(tag -> new PostTagsResDTO(
                            tag.getLabel(),
                            tag.getUuid(),
                            tag.getTagCounts(),
                            tag.getImgURL(),
                            tag.getCreateTime(),
                            tag.getUpdateTime(),
                            tag.getBlogs()
                    ))
                    .toList();

            return ResponseEntity.ok(ApiResponse.success(optionalTag));
        } catch (Exception e) {
        // 回傳錯誤 response
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("server_error", 1003));
        }
    }

    public ResponseEntity<ApiResponse<?>> editTag (
            PostTagsReqDTO request,
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
            Update update = new Update()
                    .set("label", request.getLabel());
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
