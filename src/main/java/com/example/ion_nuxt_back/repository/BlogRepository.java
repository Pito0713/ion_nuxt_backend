package com.example.ion_nuxt_back.repository;

import com.example.ion_nuxt_back.model.Blog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogRepository extends MongoRepository<Blog, String> {
    List<Blog> findByTagUUID(String tagUUID);
}
