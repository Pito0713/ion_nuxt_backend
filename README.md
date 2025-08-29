# ion_nuxt_backend
### Spring Boot + MongoDB RESTful API 文檔
使用 Spring Boot 搭配 MongoDB 所構建的簡單 RESTful API，
提供基本的 CRUD 功能。

---

## 技術棧
- Java 17+
- Spring Boot 3.x
- Spring Data MongoDB
- MongoDB Atlas & MongoDBCompass

---

## 專案結構
```
src/main/java/com/example/ion_nuxt_back/
├── config
│  
├── controller
│   ├── BlogController.java
│   ├── TagsController.java
│   └── UserController.java
├── dto
├── model
│   ├── Blog.java
│   ├── Tags.java
│   └── User.java
├── repository
│   ├── BlogRepository.java
│   ├── TagsRepository.java
│   └── UserRepository.java
├── service
│   ├── BlogService.java
│   ├── TagsService.java
│   └── UserRService.java
└── ionNuxtBackApplication.java
```

---

## 3. Controller

### BlogController.java
```java
@PostMapping // Post 新增 Blog 文章
public Blog postBlogC() {}

@GetMapping // Get 取的 Blog 文章
public Blog getBlogC() {}

@PostMapping("/{id}")  // Post 編輯 Blog 文章
public Blog editBlogC() {}

@DeleteMapping("/{id}")    // Delete 刪除 Blog 文章
public Blog deleteBlogC() {}
```

### TagsController.java
```java
@PostMapping    // Post 新增 tags 標籤;
public Tags postTagsC() {}

@GetMapping     // Get 取得 tags 標籤;
public Tags getTagsC() {}

@PostMapping("/{uuid}")     // Post 編輯 Tags 標籤;
public Tags editBlogC() {}

@DeleteMapping("/{uuid}")   // Delete 刪除 Tags 文章
public Tags deleteBlogC() {}
```

### UserController.java
```java
@PostMapping    // POST 新增使用者
public User registerUserC() {}

@PostMapping("/login")    // POST 登入
public User logInUserC() {}

@GetMapping("/{uuid}")       // GET 使用者info
public User getUserInfoC() {}

@PostMapping("/token/refresh")    // Post 重新取得 token
public User refreshTokenC() {}

@PostMapping("/userPassword")    // POST 修改密碼
public User userPasswordChangeC() {}
```

---

## 4. application.properties
```properties
spring.data.mongodb.uri=mongodb+srv://${mongodb.username}:${mongodb.password}@${mongodb.cluster}/${mongodb.database}
mongodb.username= your mongodb repository ID
mongodb.password= your password
mongodb.cluster= cluster URI
mongodb.database= Data Local
```
使用 MongoDB Atlas，替換對應參數

---
