package com.springboot.rest.quizmania.common;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.domain.Score;

public class TestData {

    public static final String USER_ID = "userId-1234";

    public static final String UNIQUE_USERNAME = "test";

    public static final String CONFIRMATION_TOKEN = "token-1234";

    public static CustomUser DISABLED_USER = CustomUser.builder()
                                             .id(USER_ID)
                                             .email("test@gmail.com")
                                             .username(UNIQUE_USERNAME)
                                             .password("pass")
                                             .role("USER")
                                             .build();

    public static CustomUser ENABLED_USER = CustomUser.builder()
                                                       .id(USER_ID)
                                                       .email("test@gmail.com")
                                                       .username(UNIQUE_USERNAME)
                                                       .password("pass")
                                                       .role("USER")
                                                       .enabled(true)
                                                       .build();

    public static final String QUESTION_ID = "questionId-1234";

    public static final Question QUESTION = Question.builder()
                                                    .id(QUESTION_ID)
                                                    .question("test question")
                                                    .answers(List.of("a","b", "c"))
                                                    .correctAnswer("a")
                                                    .build();


    public static final String QUIZ_ID = "testId-1234";

    public static final Quiz UNSAVED_QUIZ = Quiz.builder()
                                                .title("test title")
                                                .category("test category")
                                                .level(DifficultyLevel.EASY)
                                                .isPublic(true)
                                                .questionIds(List.of("q-123", "q-456", "q-789"))
                                                .build();

    public static final Quiz SAVED_PUBLIC_QUIZ = Quiz.builder()
                                                     .id(QUIZ_ID)
                                                     .title("test title")
                                                     .category("test category")
                                                     .level(DifficultyLevel.EASY)
                                                     .isPublic(true)
                                                     .questionIds(List.of("q-123", "q-456", "q-789"))
                                                     .authorId(USER_ID)
                                                     .build();

    public static final Quiz SAVED_PRIVATE_QUIZ = Quiz.builder()
                                                      .id("testId-2345")
                                                      .title("another test title")
                                                      .category("test category")
                                                      .level(DifficultyLevel.EASY)
                                                      .isPublic(false)
                                                      .questionIds(List.of("q-321", "q-654"))
                                                      .authorId(USER_ID)
                                                      .build();

    public static final List<Quiz> ALL_QUIZZES = List.of(SAVED_PUBLIC_QUIZ, SAVED_PRIVATE_QUIZ);


    public static final String SCORE_ID = "scoreId-1234";

    public static final Date endDate = Date.from( Instant.ofEpochSecond( 1572868212 ) );

    public static final Score SAVED_SCORE = Score.builder()
                                                 .id(SCORE_ID)
                                                 .quizId(QUIZ_ID)
                                                 .userId(USER_ID)
                                                 .startDate(new Date(endDate.getTime()-12000))
                                                 .endDate(endDate)
                                                 .elapsedTimeInMs(12000)
                                                 .userAnswers(Map.of("qId-1", "good answer", "qId-2", "bad answer"))
                                                 .goodAnswers(1)
                                                 .allAnswers(2)
                                                 .percentageScore(50)
                                                 .build();

    public static final Score ANOTHER_SAVED_SCORE = Score.builder()
                                                          .id("scoreId-2")
                                                          .quizId(QUIZ_ID)
                                                          .userId("userId-5678")
                                                          .startDate(new Date(endDate.getTime()-10000))
                                                          .endDate(endDate)
                                                          .elapsedTimeInMs(10000)
                                                          .userAnswers(Map.of("qId-1", "bad answer", "qId-2", "bad answer"))
                                                          .goodAnswers(0)
                                                          .allAnswers(2)
                                                          .percentageScore(0)
                                                          .build();

    public static final List<Score> ALL_SCORES = List.of(SAVED_SCORE, ANOTHER_SAVED_SCORE);
}
