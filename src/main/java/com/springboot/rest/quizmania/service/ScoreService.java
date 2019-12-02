package com.springboot.rest.quizmania.service;

import java.util.List;

import com.springboot.rest.quizmania.domain.Score;

public interface ScoreService {

    Score addScore(String username, Score score);
    Score getScoreById(String id);
    List<Score> getScoresByUser(String username);
    List<Score> getScoresByQuizId(String quizId);
}
