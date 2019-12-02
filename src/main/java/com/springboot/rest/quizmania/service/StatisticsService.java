package com.springboot.rest.quizmania.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.dto.ScoreDto;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

    private final ScoreService scoreService;

    private enum Stat {
        AVG, MIN, MAX
    }

    public StatisticsService(ScoreService scoreService) {
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

    public StatisticsDto getQuizStatisticsById(String username, String quizId, boolean globalFlag) {

        List<Score> quizScores;
        if(globalFlag)
            quizScores = scoreService.getScoresByQuizId(quizId);
        else
            quizScores = scoreService
                .getScoresByUser(username)
                .stream()
                .filter(score -> score.getQuizId().equals(quizId))
                .collect(Collectors.toList());

        List<ScoreDto> scoreDtoList = quizScores
            .stream()
            .map(score -> new ScoreDto(score.getId(), score.getElapsedTimeInMs(), score.getPercentageScore(), score.getStartDate()))
            .collect(Collectors.toList());

        return createStatistics(scoreDtoList);
    }

    public List<ScoreDto> getQuizRankingById(String quizId) {

        List<Score> quizScores = scoreService.getScoresByQuizId(quizId);

        return quizScores
            .stream()
            .map(score -> new ScoreDto(score.getId(), score.getElapsedTimeInMs(), score.getPercentageScore(), score.getStartDate()))
            .sorted(Comparator.comparingDouble(ScoreDto::getPercentageScore).reversed().thenComparingLong(ScoreDto::getElapsedTime))
            .collect(Collectors.toList());
    }
}
