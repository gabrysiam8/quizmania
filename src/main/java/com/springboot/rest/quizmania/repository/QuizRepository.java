package com.springboot.rest.quizmania.repository;

import com.springboot.rest.quizmania.domain.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuizRepository extends MongoRepository<Quiz, String> {

}
