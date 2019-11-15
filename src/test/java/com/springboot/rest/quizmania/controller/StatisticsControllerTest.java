package com.springboot.rest.quizmania.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.springboot.rest.quizmania.dto.ScoreDto;
import com.springboot.rest.quizmania.dto.StatisticsDto;
import com.springboot.rest.quizmania.service.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.ALL_SCORES;
import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService service;

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnGlobalQuizStatisticsWhenQuizIdExistAngGlobalFlagIsTrue() throws Exception {
        int attemptsNum = 2;
        double avgScore = 25;
        double bestScore = 50;
        double worstScore = 0;
        long avgTimeInMs = 11000;
        List<ScoreDto> scoreDtos = ALL_SCORES.stream()
                                             .map(score -> new ScoreDto(score.getId(), score.getElapsedTimeInMs(), score.getPercentageScore(), score.getStartDate()))
                                             .collect(Collectors.toList());
        StatisticsDto statisticsDto = createStatisticsDto(attemptsNum, avgScore, bestScore, worstScore, avgTimeInMs, scoreDtos);
        given(service.getQuizStatisticsById(UNIQUE_USERNAME, QUIZ_ID, true)).willReturn(statisticsDto);

        mockMvc.perform(get("/statistics?quizId="+QUIZ_ID+"&global=true"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.attemptsNumber").value(attemptsNum))
               .andExpect(jsonPath("$.averageScore").value(avgScore))
               .andExpect(jsonPath("$.bestScore").value(bestScore))
               .andExpect(jsonPath("$.worstScore").value(worstScore))
               .andExpect(jsonPath("$.averageTimeInMs").value(avgTimeInMs));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnUserQuizStatisticsWhenQuizIdExistAngGlobalFlagIsFalse() throws Exception {
        int attemptsNum = 1;
        double avgScore = 50;
        double bestScore = 50;
        double worstScore = 50;
        long avgTimeInMs = 12000;
        List<ScoreDto> scoreDtos = ALL_SCORES.subList(0, 1).stream()
                                             .map(score -> new ScoreDto(score.getId(), score.getElapsedTimeInMs(), score.getPercentageScore(), score.getStartDate()))
                                             .collect(Collectors.toList());
        StatisticsDto statisticsDto = createStatisticsDto(attemptsNum, avgScore, bestScore, worstScore, avgTimeInMs, scoreDtos);
        given(service.getQuizStatisticsById(UNIQUE_USERNAME, QUIZ_ID, false)).willReturn(statisticsDto);

        mockMvc.perform(get("/statistics?quizId="+QUIZ_ID+"&global=false"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.attemptsNumber").value(attemptsNum))
               .andExpect(jsonPath("$.averageScore").value(avgScore))
               .andExpect(jsonPath("$.bestScore").value(bestScore))
               .andExpect(jsonPath("$.worstScore").value(worstScore))
               .andExpect(jsonPath("$.averageTimeInMs").value(avgTimeInMs));
    }

    private StatisticsDto createStatisticsDto(int attemptsNum, double avgScore, double bestScore, double worstScore, long avgTime, List<ScoreDto> scoreDtos) {
        return StatisticsDto.builder()
                            .attemptsNumber(attemptsNum)
                            .averageScore(avgScore)
                            .bestScore(bestScore)
                            .worstScore(worstScore)
                            .averageTimeInMs(avgTime)
                            .scoreDtoList(scoreDtos)
                            .build();
    }
}