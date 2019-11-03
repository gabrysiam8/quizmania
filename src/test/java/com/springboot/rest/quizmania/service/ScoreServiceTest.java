package com.springboot.rest.quizmania.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.repository.ScoreRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private AuthService authService;

    @Mock
    private QuestionService questionService;

    private ScoreService scoreService;

    private Score savedScore;

    private CustomUser user;

    private static final String UNIQUE_USERNAME = "test";
    private static final String SCORE_ID = "scoreId-1234";
    private static final String QUIZ_ID = "quizId-1234";

    @Before
    public void setUp() {
        scoreService = new ScoreService(scoreRepository, authService, questionService);

        long elapsedTime = 12000;
        Date endDate = new Date();
        Date startDate = new Date(endDate.getTime()-elapsedTime);

        savedScore = Score.builder()
                     .id(SCORE_ID)
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

        user = CustomUser.builder()
                           .id("userId-1234")
                           .email("test@gmail.com")
                           .username(UNIQUE_USERNAME)
                           .password("pass")
                           .enabled(true)
                           .build();
    }

    @Test
    public void shouldAddScore() {
        //given
        Score score = Score.builder()
                     .quizId(QUIZ_ID)
                     .startDate(savedScore.getStartDate())
                     .endDate(savedScore.getEndDate())
                     .userAnswers(Map.of("qId-1", "good answer", "qId-2", "bad answer"))
                     .build();
        when(authService.findUserByUsername(anyString())).thenReturn(user);
        List<Question> questions = new ArrayList<>();
        score.getUserAnswers().keySet().forEach(id -> questions.add(createQuestion(id)));
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0), questions.get(1));
        when(scoreRepository.save(any(Score.class))).thenReturn(savedScore);

        //when
        Score result = scoreService.addScore(UNIQUE_USERNAME, score);

        //then
        verify(authService, times(1)).findUserByUsername(anyString());
        verify(questionService, times(2)).getQuestionById(anyString());
        verify(scoreRepository, times(1)).save(any(Score.class));
        assertNotNull(result);
        System.out.println(result);
        assertEquals(SCORE_ID, result.getId());
        assertEquals(user.getId(), result.getUserId());
    }

    @Test
    public void shouldGetScoreById() {
        //given
        when(scoreRepository.findById(anyString())).thenReturn(Optional.ofNullable(savedScore));

        //when
        Score result = scoreService.getScoreById(SCORE_ID);

        //then
        verify(scoreRepository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(SCORE_ID, result.getId());
        assertEquals(QUIZ_ID, result.getQuizId());
        assertEquals(savedScore.getUserId(), result.getUserId());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenQuizNotExist() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No score with that id exists!");
        when(scoreRepository.findById(anyString())).thenReturn(Optional.empty());

        //when
        scoreService.getScoreById(SCORE_ID);

        //then
        verify(scoreRepository, times(1)).findById(anyString());
    }

    @Test
    public void shouldGetScoresByUser() {
        //given
        when(authService.findUserByUsername(anyString())).thenReturn(user);
        when(scoreRepository.getScoresByUserId(anyString())).thenReturn(List.of(savedScore));

        //when
        List<Score> result = scoreService.getScoresByUser(UNIQUE_USERNAME);

        //then
        verify(authService, times(1)).findUserByUsername(anyString());
        verify(scoreRepository, times(1)).getScoresByUserId(anyString());
        assertNotNull(result);
        assertEquals(1, result.size());
        result.forEach(score -> assertEquals(user.getId(), score.getUserId()));
    }

    @Test
    public void shouldGetScoresByQuizId() {
        //given
        Date endDate = new Date();
        Score newScore = Score.builder()
                              .id("scoeId-5678")
                              .quizId(QUIZ_ID)
                              .userId("userId-5678")
                              .startDate(new Date(endDate.getTime()-10000))
                              .endDate(endDate)
                              .userAnswers(Map.of("qId-1", "bad answer", "qId-2", "bad answer"))
                              .build();

        when(scoreRepository.getScoresByQuizId(anyString())).thenReturn(List.of(savedScore, newScore));

        //when
        List<Score> result = scoreService.getScoresByQuizId(QUIZ_ID);

        //then
        verify(scoreRepository, times(1)).getScoresByQuizId(anyString());
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(score -> assertEquals(QUIZ_ID, score.getQuizId()));
    }

    private Question createQuestion(String id) {
        return Question.builder()
                       .id(id)
                       .question("question"+id)
                       .answers(List.of("good answer","bad answer"))
                       .correctAnswer("good answer")
                       .build();
    }
}