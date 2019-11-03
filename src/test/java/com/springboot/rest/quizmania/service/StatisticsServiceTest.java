package com.springboot.rest.quizmania.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    @Mock
    private ScoreService scoreService;

    private StatisticsService statisticsService;

    private List<Score> allScores;

    private static final String UNIQUE_USERNAME = "test";
    private static final String QUIZ_ID = "quizId-1234";

    @Before
    public void setUp() {
        statisticsService = new StatisticsService(scoreService);

        long elapsedTime = 12000;
        Date endDate = new Date();
        Date startDate = new Date(endDate.getTime()-elapsedTime);

        Score score = Score.builder()
                          .id("scoreId-1")
                          .quizId(QUIZ_ID)
                          .userId("userId-1234")
                          .startDate(startDate)
                          .endDate(endDate)
                          .elapsedTimeInMs(elapsedTime)
                          .userAnswers(Map.of("qId-1", "good answer", "qId-2", "bad answer"))
                          .goodAnswers(1)
                          .allAnswers(2)
                          .percentageScore(50)
                          .build();

        Score anotherUserScore = Score.builder()
                               .id("scoreId-2")
                               .quizId(QUIZ_ID)
                               .userId("userId-5678")
                               .startDate(startDate)
                               .endDate(endDate)
                               .elapsedTimeInMs(elapsedTime)
                               .userAnswers(Map.of("qId-1", "bad answer", "qId-2", "bad answer"))
                               .goodAnswers(0)
                               .allAnswers(2)
                               .percentageScore(0)
                               .build();

        allScores = List.of(score, anotherUserScore);
    }

    @Test
    public void shouldGetGlobalQuizStatisticsByIdWhenGlobalFlagIsTrue() {
        //given
        when(scoreService.getScoresByQuizId(anyString())).thenReturn(allScores);

        //when
        StatisticsDto result = statisticsService.getQuizStatisticsById(UNIQUE_USERNAME, QUIZ_ID, true);

        //then
        verify(scoreService, times(1)).getScoresByQuizId(anyString());
        assertNotNull(result);
        assertEquals(allScores.size(), result.getAttemptsNumber());
        assertEquals(calculateAverageScore(allScores), result.getAverageScore(), 0.000001);
    }

    @Test
    public void shouldGetUserQuizStatisticsByIdWhenGlobalFlagIsFalse() {
        //given
        List<Score> userScores = allScores.subList(0, 1);
        when(scoreService.getScoresByUser(anyString())).thenReturn(userScores);

        //when
        StatisticsDto result = statisticsService.getQuizStatisticsById(UNIQUE_USERNAME, QUIZ_ID, false);

        //then
        verify(scoreService, times(1)).getScoresByUser(anyString());
        assertNotNull(result);
        assertEquals(userScores.size(), result.getAttemptsNumber());
        assertEquals(calculateAverageScore(userScores), result.getAverageScore(), 0.000001);
    }

    private double calculateAverageScore(List<Score> scores) {
        return scores
            .stream()
            .mapToDouble(Score::getPercentageScore)
            .average()
            .orElse(0.0);
    }
}