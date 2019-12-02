package com.springboot.rest.quizmania.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

import static com.springboot.rest.quizmania.common.TestData.ENABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.SAVED_SCORE;
import static com.springboot.rest.quizmania.common.TestData.SCORE_ID;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.USER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScoreServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ScoreRepository scoreRepository;

    @Mock
    private UserFinderService userFinderService;

    @Mock
    private QuestionService questionService;

    private ScoreServiceImpl scoreService;

    @Before
    public void setUp() {
        scoreService = new ScoreServiceImpl(scoreRepository, userFinderService, questionService);
    }

    @Test
    public void shouldAddScore() {
        //given
        Score score = Score.builder()
                     .quizId(QUIZ_ID)
                     .startDate(SAVED_SCORE.getStartDate())
                     .endDate(SAVED_SCORE.getEndDate())
                     .userAnswers(Map.of("qId-1", "good answer", "qId-2", "bad answer"))
                     .build();
        when(userFinderService.findUserByUsername(anyString())).thenReturn(ENABLED_USER);
        List<Question> questions = new ArrayList<>();
        score.getUserAnswers().keySet().forEach(id -> questions.add(createQuestion(id)));
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0), questions.get(1));
        when(scoreRepository.save(any(Score.class))).thenReturn(SAVED_SCORE);

        //when
        Score result = scoreService.addScore(UNIQUE_USERNAME, score);

        //then
        verify(userFinderService, times(1)).findUserByUsername(anyString());
        verify(questionService, times(2)).getQuestionById(anyString());
        verify(scoreRepository, times(1)).save(any(Score.class));
        assertNotNull(result);
        System.out.println(result);
        assertEquals(SCORE_ID, result.getId());
        assertEquals(USER_ID, result.getUserId());
    }

    @Test
    public void shouldGetScoreById() {
        //given
        when(scoreRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_SCORE));

        //when
        Score result = scoreService.getScoreById(SCORE_ID);

        //then
        verify(scoreRepository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(SCORE_ID, result.getId());
        assertEquals(QUIZ_ID, result.getQuizId());
        assertEquals(SAVED_SCORE.getUserId(), result.getUserId());
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
        when(userFinderService.findUserByUsername(anyString())).thenReturn(ENABLED_USER);
        when(scoreRepository.getScoresByUserId(anyString())).thenReturn(List.of(SAVED_SCORE));

        //when
        List<Score> result = scoreService.getScoresByUser(UNIQUE_USERNAME);

        //then
        verify(userFinderService, times(1)).findUserByUsername(anyString());
        verify(scoreRepository, times(1)).getScoresByUserId(anyString());
        assertNotNull(result);
        assertEquals(1, result.size());
        result.forEach(score -> assertEquals(USER_ID, score.getUserId()));
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

        when(scoreRepository.getScoresByQuizId(anyString())).thenReturn(List.of(SAVED_SCORE, newScore));

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