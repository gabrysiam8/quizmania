package com.springboot.rest.quizmania.repository;

import com.springboot.rest.quizmania.domain.Score;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScoreRepository extends MongoRepository<Score, String> {

}
