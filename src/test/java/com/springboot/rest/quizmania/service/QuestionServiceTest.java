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

    private Question question;

    private static final String QUESTION_ID = "question-1234";

    @Before
    public void setUp() {
        questionService = new QuestionService(questionRepository);

        question = Question.builder()
                         .id(QUESTION_ID)
                         .question("test question")
                         .answers(List.of("a","b", "c"))
                         .correctAnswer("a")
                         .build();
    }

    @Test
    public void shouldAddQuestion() {
        //given
        when(questionRepository.save(question)).thenReturn(question);

        //when
        Question result = questionService.addQuestion(question);

        //then
        verify(questionRepository, times(1)).save(any(Question.class));
        assertNotNull(result);
        assertEquals(question, result);
    }

    @Test
    public void shouldReturnQuestion() {
        //given
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.ofNullable(question));

        //when
        Question result = questionService.getQuestionById(QUESTION_ID);

        //then
        verify(questionRepository, times(1)).findById(anyString());
        assertNotNull(result);
        assertEquals(question, result);
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
        Question questionUpdate = question;
        questionUpdate.setQuestion("updated question");
        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.ofNullable(question));
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