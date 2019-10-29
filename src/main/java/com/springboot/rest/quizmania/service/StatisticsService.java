package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.dto.ScoreDto;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final QuizService quizService;

    private final ScoreService scoreService;

    private enum Stat {
        AVG, MIN, MAX
    }

    public StatisticsService(QuizService quizService, ScoreService scoreService) {
        this.quizService = quizService;
        this.scoreService = scoreService;
    }

    private StatisticsDto createStatistics(List<ScoreDto> scoreList) {
        StatisticsDto stats = new StatisticsDto();

        stats.setAttemptsNumber(scoreList.size());

        double avgScore = calculateScoreStats(scoreList, Stat.AVG);
        stats.setAverageScore(Math.round((avgScore*100.0))/100.0);

        stats.setBestScore(calculateScoreStats(scoreList, Stat.MAX));
        stats.setWorstScore(calculateScoreStats(scoreList, Stat.MIN));

        stats.setAverageTimeInMs(calculateAvgTime(scoreList));

        stats.setScoreDtoList(scoreList);
        return stats;
    }

    private double calculateScoreStats(List<ScoreDto> scoreList, Stat statOption) {
        DoubleStream percentageScores = scoreList
            .stream()
            .mapToDouble(ScoreDto::getPercentageScore);

        switch (statOption) {
            case AVG:
                return percentageScores
                    .average()
                    .orElse(0.0);
            case MAX:
                return percentageScores
                    .max()
                    .orElse(0.0);
            case MIN:
                return percentageScores
                    .min()
                    .orElse(0.0);
        }
        return 0.0;
    }

    private double calculateAvgTime(List<ScoreDto> scoreList) {
        return scoreList
            .stream()
            .mapToLong(ScoreDto::getElapsedTime)
            .average()
            .orElse(0.0);
    }

    public StatisticsDto getQuizStatisticsById(String quizId) {
        List<Score> quizScores = scoreService.getScoresByQuizId(quizId);

        List<ScoreDto> scoreDtoList = quizScores
            .stream()
            .map(score -> new ScoreDto(score.getElapsedTimeInMs(), score.getPercentageScore()))
            .collect(Collectors.toList());

        return createStatistics(scoreDtoList);
    }
}
