package com.springboot.rest.quizmania.repository;

import com.springboot.rest.quizmania.domain.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<CustomUser, String> {
    CustomUser findByEmail(String email);
    CustomUser findByUsername(String username);
}
