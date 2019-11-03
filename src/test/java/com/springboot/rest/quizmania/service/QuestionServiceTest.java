package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.Optional;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.springboot.rest.quizmania.common.TestData.QUESTION;
import static com.springboot.rest.quizmania.common.TestData.QUESTION_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private QuestionRepository questionRepository;

    private QuestionService questionService;

    @Before
    public void setUp() {
        questionService = new QuestionService(questionRepository);
    }

    @Test
    public void shouldAddQuestion() {
        //given
        when(questionRepository.save(any(Question.class))).thenReturn(QUESTION);

        //when
        Question result = questionService.addQuestion(QUESTION);

        //then
        verify(questionRepository, times(1)).save(any(Question.class));
        assertNotNull(result);
        assertEquals(QUESTION, result);
    }

    @Test
    public void shouldReturnQuestion() {
        //given
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.ofNullable(QUESTION));

        //when
        Question result = questionService.getQuestionById(QUESTION_ID);

        //then
        verify(questionRepository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(QUESTION, result);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidQuestionId() {
        //given
        String invalidQuestionId = "invalidId-1234";
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No question with that id exists!");
        when(questionRepository.findById(invalidQuestionId)).thenReturn(Optional.empty());

        //when
        questionService.getQuestionById(invalidQuestionId);

        //then
        verify(questionRepository, times(1)).findById(anyString());
    }

    @Test
    public void shouldUpdateQuestion() {
        //given
        Question questionUpdate = Question.builder()
                                          .id(QUESTION_ID)
                                          .question("updated question")
                                          .answers(List.of("a","b", "c"))
                                          .correctAnswer("a")
                                          .build();
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.ofNullable(QUESTION));
        when(questionRepository.save(questionUpdate)).thenReturn(questionUpdate);

        //when
        Question result = questionService.updateQuestion(QUESTION_ID, questionUpdate);

        //then
        verify(questionRepository, times(1)).findById(anyString());
        verify(questionRepository, times(1)).save(any(Question.class));
        assertNotNull(result);
        assertEquals(questionUpdate.getQuestion(), result.getQuestion());
    }

    @Test
    public void shouldDeleteQuestion() {
        //given
        when(questionRepository.existsById(QUESTION_ID)).thenReturn(true);

        //when
        String result = questionService.deleteQuestion(QUESTION_ID);

        //then
        verify(questionRepository, times(1)).existsById(anyString());
        verify(questionRepository, times(1)).deleteById(anyString());
        assertNotNull(result);
        assertEquals(result, "Question successfully deleted");
    }
}