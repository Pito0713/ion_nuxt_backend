package com.example.ion_nuxt_back.repository;
import com.example.ion_nuxt_back.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    // MongoDB 語法
    Optional<User> findByAccount(String account); // MongoDB find Account
    Optional<User> findByUuid(String uuid); // MongoDB find Account
}