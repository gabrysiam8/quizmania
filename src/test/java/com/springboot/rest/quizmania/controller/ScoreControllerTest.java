package com.springboot.rest.quizmania.controller;

import java.util.List;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.service.ScoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.SAVED_SCORE;
import static com.springboot.rest.quizmania.common.TestData.SCORE_ID;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.USER_ID;
import static com.springboot.rest.quizmania.common.TestUtils.readFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScoreService service;

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnNewScoreWhenSuccessfullyAdded() throws Exception {
        given(service.addScore(anyString(), any(Score.class))).willReturn(SAVED_SCORE);

        mockMvc.perform(post("/score")
            .content(readFile("requests/score.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(SCORE_ID))
               .andExpect(jsonPath("$.quizId").value(QUIZ_ID))
               .andExpect(jsonPath("$.userId").value(USER_ID))
               .andExpect(jsonPath("$.startDate").value("2019-11-04T11:50:00.000+00:00"))
               .andExpect(jsonPath("$.endDate").value("2019-11-04T11:50:12.000+00:00"));
    }

    @Test
    public void shouldReturnScoreWhenIdExist() throws Exception {
        given(service.getScoreById(anyString())).willReturn(SAVED_SCORE);

        mockMvc.perform(get("/score/"+SCORE_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(SCORE_ID))
               .andExpect(jsonPath("$.quizId").value(QUIZ_ID))
               .andExpect(jsonPath("$.userId").value(USER_ID))
               .andExpect(jsonPath("$.startDate").value("2019-11-04T11:50:00.000+00:00"))
               .andExpect(jsonPath("$.endDate").value("2019-11-04T11:50:12.000+00:00"));
    }

    @Test
    public void shouldReturnScoreWhenIdNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("No score with that id exists!");
        given(service.getScoreById(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/score/invalidId-1234"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnAllUserScores() throws Exception {
        given(service.getScoresByUser(anyString())).willReturn(List.of(SAVED_SCORE));

        mockMvc.perform(get("/score"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(SCORE_ID))
               .andExpect(jsonPath("$[0].quizId").value(QUIZ_ID))
               .andExpect(jsonPath("$[0].userId").value(USER_ID))
               .andExpect(jsonPath("$[0].startDate").value("2019-11-04T11:50:00.000+00:00"))
               .andExpect(jsonPath("$[0].endDate").value("2019-11-04T11:50:12.000+00:00"));
    }
}
