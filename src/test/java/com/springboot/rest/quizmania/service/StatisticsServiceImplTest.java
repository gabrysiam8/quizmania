package com.springboot.rest.quizmania.service;

import java.util.List;
import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.springboot.rest.quizmania.common.TestData.ALL_SCORES;
import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceImplTest {

    @Mock
    private ScoreService scoreService;

    private StatisticsServiceImpl statisticsService;

    @Before
    public void setUp() {
        statisticsService = new StatisticsServiceImpl(scoreService);
    }

    @Test
    public void shouldGetGlobalQuizStatisticsByIdWhenGlobalFlagIsTrue() {
        //given
        when(scoreService.getScoresByQuizId(anyString())).thenReturn(ALL_SCORES);

        //when
        StatisticsDto result = statisticsService.getQuizStatisticsById(UNIQUE_USERNAME, QUIZ_ID, true);

        //then
        verify(scoreService, times(1)).getScoresByQuizId(anyString());
        assertNotNull(result);
        assertEquals(ALL_SCORES.size(), result.getAttemptsNumber());
        assertEquals(calculateAverageScore(ALL_SCORES), result.getAverageScore(), 0.000001);
    }

    @Test
    public void shouldGetUserQuizStatisticsByIdWhenGlobalFlagIsFalse() {
        //given
        List<Score> userScores = ALL_SCORES.subList(0, 1);
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