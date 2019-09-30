package com.springboot.rest.quizmania.repository;

import com.springboot.rest.quizmania.domain.Question;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QuestionRepository extends MongoRepository<Question, String> {

}
