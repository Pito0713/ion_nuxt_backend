package com.example.ion_nuxt_back.repository;

import com.example.ion_nuxt_back.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BlogRepository extends MongoRepository<Blog, String> {
}
