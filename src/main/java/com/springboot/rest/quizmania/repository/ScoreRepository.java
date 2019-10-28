package com.springboot.rest.quizmania.repository;

import java.util.List;

import com.springboot.rest.quizmania.domain.Score;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScoreRepository extends MongoRepository<Score, String> {
    List<Score> getScoresByUserId(String userId);
}
