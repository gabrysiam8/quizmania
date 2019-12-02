package com.springboot.rest.quizmania.service;

import java.util.List;

import com.springboot.rest.quizmania.dto.ScoreDto;
import com.springboot.rest.quizmania.dto.StatisticsDto;

public interface StatisticsService {

    StatisticsDto getQuizStatisticsById(String username, String quizId, boolean globalFlag);
    List<ScoreDto> getQuizRankingById(String quizId);
}
