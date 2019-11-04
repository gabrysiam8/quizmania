package com.springboot.rest.quizmania.controller;

import java.util.List;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.service.QuestionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.QUESTION;
import static com.springboot.rest.quizmania.common.TestData.QUESTION_ID;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestUtils.readFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService service;

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnNewQuestionWhenSuccessfullyAdded() throws Exception {
        given(service.addQuestion(any(Question.class))).willReturn(QUESTION);

        mockMvc.perform(post("/question")
            .content(readFile("requests/question.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(QUESTION_ID))
               .andExpect(jsonPath("$.question").value("test question"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenInvalidQuestionContent() throws Exception {
        given(service.addQuestion(any(Question.class))).willReturn(QUESTION);

        mockMvc.perform(post("/question")
            .content("{}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnQuestionWhenIdExist() throws Exception {
        given(service.getQuestionById(anyString())).willReturn(QUESTION);

        mockMvc.perform(get("/question/"+QUESTION_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(QUESTION_ID))
               .andExpect(jsonPath("$.question").value("test question"));
    }

    @Test
    public void shouldReturnBadRequestWhenIdNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("No question with that id exists!");
        given(service.getQuestionById(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/question/invalidId-1234"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnUpdatedQuestionWhenIdExist() throws Exception {
        Question questionUpdate = Question.builder()
                                    .id(QUESTION_ID)
                                    .question("updated question")
                                    .answers(List.of("a","b", "c"))
                                    .correctAnswer("a")
                                    .build();
        given(service.updateQuestion(anyString(), any(Question.class))).willReturn(questionUpdate);

        mockMvc.perform(put("/question/"+QUESTION_ID)
               .content(readFile("requests/question-update.json"))
               .contentType(APPLICATION_JSON))
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.id").value(QUESTION_ID))
                   .andExpect(jsonPath("$.question").value("updated question"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnSuccessDeleteMessageWhenIdExist() throws Exception {
        given(service.deleteQuestion(anyString())).willReturn("Question successfully deleted");

        mockMvc.perform(delete("/question/"+QUESTION_ID))
               .andExpect(status().isOk())
               .andExpect(content().string("Question successfully deleted"));
    }
}