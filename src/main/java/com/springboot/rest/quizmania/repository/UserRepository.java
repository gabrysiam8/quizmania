package com.springboot.rest.quizmania.repository;

import java.util.List;

import com.springboot.rest.quizmania.domain.CustomUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<CustomUser, String> {
    CustomUser findByEmail(String email);
    CustomUser findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Override
    List<CustomUser> findAll();
}
