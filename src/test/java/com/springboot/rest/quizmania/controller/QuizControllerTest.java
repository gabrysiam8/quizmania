package com.springboot.rest.quizmania.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.service.QuizService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.ALL_QUIZZES;
import static com.springboot.rest.quizmania.common.TestData.QUESTION_ID;
import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.SAVED_PUBLIC_QUIZ;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.USER_ID;
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
public class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService service;

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnNewQuizWhenSuccessfullyAdded() throws Exception {
        given(service.addQuiz(anyString(), any(Quiz.class))).willReturn(SAVED_PUBLIC_QUIZ);

        mockMvc.perform(post("/quiz")
            .content(readFile("requests/quiz.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(QUIZ_ID))
               .andExpect(jsonPath("$.title").value("test title"))
               .andExpect(jsonPath("$.category").value("test category"))
               .andExpect(jsonPath("$.level").value("EASY"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnBadRequestWhenInvalidQuestionContent() throws Exception {
        given(service.addQuiz(anyString(), any(Quiz.class))).willReturn(SAVED_PUBLIC_QUIZ);

        mockMvc.perform(post("/quiz")
            .content("{}")
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnAllPublicQuizzes() throws Exception {
        given(service.getAllPublicQuizzes()).willReturn(List.of(SAVED_PUBLIC_QUIZ));

        mockMvc.perform(get("/quiz/all"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(1))
               .andExpect(jsonPath("$[0].id").value(QUIZ_ID))
               .andExpect(jsonPath("$[0].title").value("test title"))
               .andExpect(jsonPath("$[0].category").value("test category"))
               .andExpect(jsonPath("$[0].level").value("EASY"))
               .andExpect(jsonPath("$[0].isPublic").value("true"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnAllUserQuizzes() throws Exception {
        given(service.getAllUserQuizzes(UNIQUE_USERNAME)).willReturn(ALL_QUIZZES);

        mockMvc.perform(get("/quiz"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(QUIZ_ID))
               .andExpect(jsonPath("$[0].title").value("test title"))
               .andExpect(jsonPath("$[0].category").value("test category"))
               .andExpect(jsonPath("$[0].level").value("EASY"))
               .andExpect(jsonPath("$[0].isPublic").value("true"))
               .andExpect(jsonPath("$[0].authorId").value(USER_ID))
               .andExpect(jsonPath("$[1].id").value("testId-2345"))
               .andExpect(jsonPath("$[1].title").value("another test title"))
               .andExpect(jsonPath("$[1].category").value("test category"))
               .andExpect(jsonPath("$[1].level").value("EASY"))
               .andExpect(jsonPath("$[1].isPublic").value("false"))
               .andExpect(jsonPath("$[1].authorId").value(USER_ID));
    }

    @Test
    public void shouldReturnQuizWithoutFilteringWhenIdExistAndNoRequestParam() throws Exception {
        given(service.getQuizById(QUIZ_ID, null)).willReturn(readFile("/requests/quiz-saved.json"));

        mockMvc.perform(get("/quiz/"+QUIZ_ID))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(QUIZ_ID))
               .andExpect(jsonPath("$.title").value("test title"))
               .andExpect(jsonPath("$.category").value("test category"))
               .andExpect(jsonPath("$.level").value("EASY"))
               .andExpect(jsonPath("$.isPublic").value("true"))
               .andExpect(jsonPath("$.authorId").value(USER_ID));
    }

    @Test
    public void shouldReturnQuizTitleWhenIdExistAndTitleFieldRequestParam() throws Exception {
        String fieldFilter = "title";
        given(service.getQuizById(QUIZ_ID, new String[]{fieldFilter})).willReturn("{\"title\": \"test title\"}");

        mockMvc.perform(get("/quiz/"+QUIZ_ID+"?fields="+fieldFilter))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("test title"));
    }

    @Test
    public void shouldReturnBadRequestWhenIdNotExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("No quiz with that id exists!");
        given(service.getQuizById(QUIZ_ID, null)).willThrow(expectedException);

        mockMvc.perform(get("/quiz/"+QUIZ_ID))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    public void shouldReturnBadRequestWhenJsonProcessingFailure() throws Exception {
        Exception expectedException = new JsonParseException(null, "JSON parse exception");
        given(service.getQuizById(QUIZ_ID, null)).willThrow(expectedException);

        mockMvc.perform(get("/quiz/"+QUIZ_ID))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    public void shouldReturnQuizQuestionsByIdWhenIdExist() throws Exception {
        List<Question> questions = createQuestions(SAVED_PUBLIC_QUIZ.getQuestionIds());
        given(service.getQuizQuestionsById(QUIZ_ID)).willReturn(questions);

        mockMvc.perform(get("/quiz/"+QUIZ_ID+"/question?toScore=true"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(3))
               .andExpect(jsonPath("$[0].id").value("q-123"))
               .andExpect(jsonPath("$[0].question").value("question q-123"))
               .andExpect(jsonPath("$[1].id").value("q-456"))
               .andExpect(jsonPath("$[1].question").value("question q-456"))
               .andExpect(jsonPath("$[2].id").value("q-789"))
               .andExpect(jsonPath("$[2].question").value("question q-789"));
    }

    @Test
    public void shouldReturnQuizQuestionDtosByIdWhenIdExist() throws Exception {
        List<QuestionDto> questionDtos = SAVED_PUBLIC_QUIZ
            .getQuestionIds()
            .stream()
            .map(id -> QuestionDto.builder()
                                  .id(id)
                                  .question("question " + id)
                                  .badAnswers(List.of("b", "c"))
                                  .correctAnswer("a")
                                  .build())
            .collect(Collectors.toList());
        given(service.getQuizQuestionDtosById(QUIZ_ID)).willReturn(questionDtos);

        mockMvc.perform(get("/quiz/"+QUIZ_ID+"/question?toScore=false"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(3))
               .andExpect(jsonPath("$[0].id").value("q-123"))
               .andExpect(jsonPath("$[0].question").value("question q-123"))
               .andExpect(jsonPath("$[1].id").value("q-456"))
               .andExpect(jsonPath("$[1].question").value("question q-456"))
               .andExpect(jsonPath("$[2].id").value("q-789"))
               .andExpect(jsonPath("$[2].question").value("question q-789"));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnUpdatedQuizWhenIdExist() throws Exception {
        Quiz quizUpdate = Quiz.builder()
                              .id(QUIZ_ID)
                              .title("updated title")
                              .category("test category")
                              .level(DifficultyLevel.EASY)
                              .isPublic(true)
                              .questionIds(List.of("q-123", "q-456", "q-789"))
                              .authorId("userId-098")
                              .build();
        given(service.updateQuiz(anyString(), any(Quiz.class))).willReturn(quizUpdate);

        mockMvc.perform(put("/quiz/"+QUIZ_ID)
            .content(readFile("requests/quiz-update.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(QUIZ_ID))
               .andExpect(jsonPath("$.title").value(quizUpdate.getTitle()));
    }

    @Test
    @WithMockUser(username=UNIQUE_USERNAME)
    public void shouldReturnSuccessDeleteMessageWhenIdExist() throws Exception {
        given(service.deleteQuiz(anyString())).willReturn("Quiz successfully deleted");

        mockMvc.perform(delete("/quiz/"+QUESTION_ID))
               .andExpect(status().isOk())
               .andExpect(content().string("Quiz successfully deleted"));
    }

    private List<Question> createQuestions(List<String> ids) {
        return ids
            .stream()
            .map(id -> Question.builder()
                               .id(id)
                               .question("question " + id)
                               .answers(List.of("a", "b", "c"))
                               .correctAnswer("a")
                               .build())
            .collect(Collectors.toList());
    }
}