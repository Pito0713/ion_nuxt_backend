package com.example.ion_nuxt_back.service;
import com.example.ion_nuxt_back.common.ApiResponse;
// DTO.requset
import com.example.ion_nuxt_back.dto.blogs.requset.PostBlogReqDTO;
// DTO.response
import com.example.ion_nuxt_back.dto.blogs.response.GetBlogResDTO;
import com.example.ion_nuxt_back.dto.blogs.response.TagDTO;
// model
import com.example.ion_nuxt_back.model.Blog;
import com.example.ion_nuxt_back.model.Tags;
// repository
import com.example.ion_nuxt_back.repository.BlogRepository;
import com.example.ion_nuxt_back.repository.TagsRepository;
// springframework
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

// util
import org.bson.types.ObjectId;
import org.jsoup.Jsoup;
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
            // param
            String title = request.getTitle();
            String textContent = request.getTextContent();
            String tagUUID = request.getTagUUID();
            // conditional: title 跟 textContent  tagUUID 判斷是否空值
            if (( title == null || title.trim().isEmpty()) || ( textContent == null || textContent.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }

            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setTextContent(textContent);
            blog.setUserUUID(userUUID);
            blog.setCreateTime(new Date());
            blog.setUpdateTime(null);
            blog.setBlogCounts(0);

            if ( tagUUID == null || tagUUID.trim().isEmpty() || tagUUID.equals("none")) {
                blog.setTagUUID("none");
            } else {
                blog.setTagUUID(tagUUID);
            }

            // 使用 Jsoup 函式庫將 HTML 轉換為純文字
            String plainText = Jsoup.parse(textContent).text();

            // 截取前 150 個字元 做預覽文字
            if (plainText.length() > 150) {
                blog.setPreviewText(plainText.substring(0, 150) + "...");
            } else {
                blog.setPreviewText(plainText);
            }

            blogRepository.save(blog);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // Get 取得 Blog 文章
    public ResponseEntity<ApiResponse<?>> getBlog(
            Integer page,
            Integer pageSize,
            String sort
    ) {
        try {
            // 建立排序順序 >> 預設 DESC 降序
            Sort.Direction dir = Sort.Direction.DESC;
            String o = sort.trim().toLowerCase();
            if (o.equals("asc")) dir =  Sort.Direction.ASC;

            Sort order = Sort.by(
                    Sort.Order.by("createTime").with(dir),
                    Sort.Order.by("_id").with(dir) // 次要排序，避免時間相同時不穩定
            );

            Query query = new Query()
                    .with(order)
                    .skip(Math.max(0, (page - 1) * pageSize))
                    .limit(pageSize);

            List<Blog> blogs = mongoTemplate.find(query, Blog.class);

            // Map 每個 blog
            // 搜尋對應的 tagUUID, 並帶入對應 label ImgURL
            for (Blog blog : blogs) {
                TagDTO blogTag = null;
                if (blog.getTagUUID() != null) {
                    Optional<Tags> optionalTags = tagsRepository.findByUuid(blog.getTagUUID());
                    blogTag = new TagDTO();
                    if (optionalTags.isPresent()) {
                        Tags tagsItem = optionalTags.get();
                        blogTag.setLabel(tagsItem.getLabel());
                        blogTag.setImgURL(tagsItem.getImgURL());
                    } else {
                        blogTag.setLabel("none");
                        blogTag.setImgURL(null);
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

            // 建立一個新的 Query，保留篩選條件
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
            // conditional: id 判斷是否空值
            if (( id == null || id.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
            Blog blog = mongoTemplate.findOne(query, Blog.class); // 只會回傳單筆
            assert blog != null;
            // 查看後 Counts 新增 1 筆
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

    // Post 編輯 Blog 文章
    public ResponseEntity<ApiResponse<?>> editBlog (
            PostBlogReqDTO request,
            String id
    ) {
        try {
            // param
            String title = request.getTitle();
            String textContent = request.getTextContent();
            String tagUUID = request.getTagUUID();
            // conditional: id 判斷是否空值
            if (( id == null || id.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));

            // 使用 Jsoup 函式庫將 HTML 轉換為純文字
            String plainText;
            // 截取前 150 個字元
            if (Jsoup.parse(textContent).text().length() > 150) {
                plainText = Jsoup.parse(textContent).text().substring(0, 150) + "...";
            } else {
                plainText =  Jsoup.parse(textContent).text();
            }

            Update update = new Update()
                    .set("title", title)
                    .set("textContent", textContent)
                    .set("previewText", plainText)
                    .set("tagUUID", tagUUID)
                    .set("updateTime", new Date());
            mongoTemplate.updateFirst(query, update, Blog.class);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

    // Delete 刪除 Blog 文章
    public ResponseEntity<ApiResponse<?>> deleteBlog (
            String id
    ) {
        try {
            if (( id == null || id.trim().isEmpty())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("resource_is_Empty", 1004));
            }
            // 先檢查是否存在（MongoRepository.deleteById 不會丟 not found）
            boolean exists = blogRepository.existsById(id);
            if (!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("blog_not_found", 1010));
            }

            blogRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            // 回傳錯誤 response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("server_error", 1003));
        }
    }

}
