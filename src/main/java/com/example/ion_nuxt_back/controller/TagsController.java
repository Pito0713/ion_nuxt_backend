package com.example.ion_nuxt_back.controller;
import com.example.ion_nuxt_back.common.ApiResponse;
import com.example.ion_nuxt_back.dto.tags.response.PostEditTagsResDTO;
import com.example.ion_nuxt_back.dto.tags.response.PostTagsResDTO;
import com.example.ion_nuxt_back.service.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
public class TagsController {
    @Autowired private TagsService tagsService;

    // Post 新增 tags 標籤;
    @PostMapping
    public ResponseEntity<ApiResponse<?>> postTagsC(
            @RequestBody PostTagsResDTO request
    ) {
        return tagsService.postTags(request);
    }

    // Get 取得 tags 標籤;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getTagsC() {
        return tagsService.getTags();
    }

    // Post 編輯 Tags 標籤;
    @PostMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> editTagC (
            @RequestBody PostEditTagsResDTO request,
            @PathVariable String uuid
    ) {
        return tagsService.editTag(request, uuid);
    }

    // Delete 刪除 Tags 文章
    @DeleteMapping("/{uuid}")
    public ResponseEntity<ApiResponse<?>> deleteTagC (
            @PathVariable String uuid
    ) {
        return tagsService.deleteTag(uuid);
    }
}
