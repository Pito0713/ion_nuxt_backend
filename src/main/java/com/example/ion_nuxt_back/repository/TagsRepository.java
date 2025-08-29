package com.example.ion_nuxt_back.repository;

import com.example.ion_nuxt_back.model.Tags;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TagsRepository extends MongoRepository<Tags, String> {
    Optional<Tags> findByUuid(String uuid);
}
