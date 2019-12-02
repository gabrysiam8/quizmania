package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.Optional;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.repository.QuestionRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

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
public class QuestionServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private ModelMapper modelMapper;

    private QuestionServiceImpl questionService;

    @Before
    public void setUp() {
        questionService = new QuestionServiceImpl(questionRepository, modelMapper);
    }

    @Test
    public void shouldAddQuestion() {
        //given
        QuestionDto questionDto = QuestionDto.builder()
                                             .question("test question")
                                             .badAnswers(List.of("b", "c"))
                                             .correctAnswer("a")
                                             .build();

        when(modelMapper.map(questionDto, Question.class)).thenReturn(QUESTION);
        when(questionRepository.save(any(Question.class))).thenReturn(QUESTION);

        //when
        Question result = questionService.addQuestion(questionDto);

        //then
        verify(modelMapper, times(1)).map(any(QuestionDto.class), (Class<?>) any(Class.class));
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
        QuestionDto questionUpdateDto = QuestionDto.builder()
                                             .id(QUESTION_ID)
                                             .question("updated question")
                                             .badAnswers(List.of("b", "c"))
                                             .correctAnswer("a")
                                             .build();

        Question questionUpdate = Question.builder()
                                          .id(QUESTION_ID)
                                          .question("updated question")
                                          .answers(List.of("a", "b", "c"))
                                          .correctAnswer("a")
                                          .build();

        when(questionRepository.findById(QUESTION_ID)).thenReturn(Optional.ofNullable(QUESTION));
        when(modelMapper.map(questionUpdateDto, Question.class)).thenReturn(questionUpdate);
        when(questionRepository.save(any(Question.class))).thenReturn(questionUpdate);

        //when
        Question result = questionService.updateQuestion(QUESTION_ID, questionUpdateDto);

        //then
        verify(questionRepository, times(1)).findById(anyString());
        verify(modelMapper, times(1)).map(any(QuestionDto.class), (Class<?>) any(Class.class));
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