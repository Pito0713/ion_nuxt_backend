package com.example.ion_nuxt_back.controller;
import com.example.ion_nuxt_back.common.ApiResponse;
import com.example.ion_nuxt_back.dto.blogs.requset.PostBlogReqDTO;
import com.example.ion_nuxt_back.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blogs")
public class BlogController {
    @Autowired private BlogService blogService;

    // Post 新增 Blog 文章
    @PostMapping
    public ResponseEntity<ApiResponse<?>> postBlogC(
            @RequestBody PostBlogReqDTO request,
            @CookieValue(value = "userUUID", required = true) String userUUID
    ) {
        return blogService.postBlog(request, userUUID);
    }

    // Get 搜尋 Blog 文章
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getBlogC(
            @RequestParam int page,
            @RequestParam int pageSize
    ) {
        return blogService.getBlog(page, pageSize);
    }

    // Get 取得 Blog 文章
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getSingleBlogC(
            @PathVariable String id
    ) {
        return blogService.getSingleBlog(id);
    }

    // Post 編輯 Blog 文章
    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> editBlogC(
            @RequestBody PostBlogReqDTO request,
            @PathVariable String id
    ) {
        return blogService.editBlog(request, id);
    }

    // Delete 刪除 Blog 文章
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteBlogC(
            @PathVariable String id,
            @CookieValue(value = "userAccessToken", required = true) String userToken
    ) {
        return blogService.deleteBlog(id);
    }
}
