package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final QuizService quizService;

    private final ScoreService scoreService;

    public StatisticsService(QuizService quizService, ScoreService scoreService) {
        this.quizService = quizService;
        this.scoreService = scoreService;
    }

    public StatisticsDto getQuizStatisticsById(String quizId) {
        List<Score> quizScores = scoreService.getScoresByQuizId(quizId);

        List<Double> allPercentageScores = quizScores
            .stream()
            .map(Score::getPercentageScore)
            .collect(Collectors.toList());

        List<Long> allElapsedTimes = quizScores
            .stream()
            .map(Score::getElapsedTimeInMs)
            .collect(Collectors.toList());

        StatisticsDto stats = new StatisticsDto();

        stats.setAttemptsNumber(quizScores.size());

        double avgScore = allPercentageScores
            .stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
        stats.setAverageScore(Math.round((avgScore*100.0))/100.0);

        stats.setBestScore(allPercentageScores
            .stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0.0)
        );

        stats.setWorstScore(allPercentageScores
            .stream()
            .mapToDouble(Double::doubleValue)
            .min()
            .orElse(0.0)
        );

        double avgTime = allElapsedTimes
            .stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        stats.setAllPercentageScores(allPercentageScores);
        stats.setAverageTimeInMs(Math.round((avgTime*100.0))/100.0);

        return stats;
    }
}
