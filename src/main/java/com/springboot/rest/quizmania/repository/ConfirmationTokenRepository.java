package com.springboot.rest.quizmania.repository;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfirmationTokenRepository extends MongoRepository<ConfirmationToken, String> {
    ConfirmationToken findByToken(String token);
}
