package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.repository.QuizRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import static com.springboot.rest.quizmania.common.TestData.ALL_QUIZZES;
import static com.springboot.rest.quizmania.common.TestData.ENABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.QUIZ_ID;
import static com.springboot.rest.quizmania.common.TestData.SAVED_PUBLIC_QUIZ;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.UNSAVED_QUIZ;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuizServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private UserFinderService userFinderService;

    @Mock
    private QuestionService questionService;

    @Mock
    private ModelMapper modelMapper;

    private QuizService quizService;

    @Before
    public void setUp() {
        quizService = new QuizServiceImpl(quizRepository, userFinderService, questionService, modelMapper);
    }

    @Test
    public void shouldAddQuiz() {
        //given
        when(userFinderService.findUserByUsername(anyString())).thenReturn(ENABLED_USER);
        when(quizRepository.save(any(Quiz.class))).thenReturn(SAVED_PUBLIC_QUIZ);

        //when
        Quiz result = quizService.addQuiz(UNIQUE_USERNAME, UNSAVED_QUIZ);

        //then
        verify(userFinderService, times(1)).findUserByUsername(anyString());
        verify(quizRepository, times(1)).save(any(Quiz.class));
        assertNotNull(result);
        assertEquals(QUIZ_ID, result.getId());
        assertEquals(ENABLED_USER.getId(), result.getAuthorId());
    }

    @Test
    public void shouldGetQuizByIdWithoutFilteringWhenNoFieldGiven() throws JsonProcessingException {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_PUBLIC_QUIZ));

        //when
        String result = quizService.getQuizById(QUIZ_ID, null);

        //then
        verify(quizRepository, times(1)).findById(anyString());
        assertNotNull(result);
        String expected = "{"
            + "\"id\":\"testId-1234\","
            + "\"title\":\"test title\","
            + "\"category\":\"test category\","
            + "\"description\":null,"
            + "\"level\":\"EASY\","
            + "\"isPublic\":true,"
            + "\"questionIds\":[\"q-123\",\"q-456\",\"q-789\"],"
            + "\"authorId\":\"userId-1234\"}";
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetQuizByIdWithFilteringWhenTitleFieldGiven() throws JsonProcessingException {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_PUBLIC_QUIZ));

        //when
        String result = quizService.getQuizById(QUIZ_ID, new String[]{"title"});

        //then
        verify(quizRepository, times(1)).findById(anyString());
        assertNotNull(result);
        String expected = "{\"title\":\"test title\"}";
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenQuizNotExist() throws JsonProcessingException {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("No quiz with that id exists!");
        when(quizRepository.findById(anyString())).thenReturn(Optional.empty());

        //when
        quizService.getQuizById(QUIZ_ID, new String[]{"title"});

        //then
        verify(quizRepository, times(1)).findById(anyString());
    }

    @Test
    public void shouldGetQuizQuestionsById() {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_PUBLIC_QUIZ));
        List<Question> questions = createQuestions(SAVED_PUBLIC_QUIZ.getQuestionIds());
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0),questions.get(1),questions.get(2));

        //when
        List<Question> result = quizService.getQuizQuestionsById(QUIZ_ID);

        //then
        verify(quizRepository, times(1)).findById(anyString());
        verify(questionService, times(3)).getQuestionById(anyString());
        assertNotNull(result);
        int expectedSize = SAVED_PUBLIC_QUIZ.getQuestionIds().size();
        assertEquals(expectedSize, result.size());
        for (int i=0; i<expectedSize; i++)
            assertEquals(SAVED_PUBLIC_QUIZ.getQuestionIds().get(i), result.get(i).getId());
    }

    @Test
    public void shouldGetQuizQuestionDtosById() {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_PUBLIC_QUIZ));

        List<Question> questions = createQuestions(SAVED_PUBLIC_QUIZ.getQuestionIds());
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0),questions.get(1),questions.get(2));

        List<QuestionDto> questionDtos = questions
            .stream()
            .map(q -> new QuestionDto(q.getId(), q.getQuestion(), null, q.getCorrectAnswer()))
            .collect(Collectors.toList());
        when(modelMapper.map(any(Question.class), any(Class.class))).thenReturn(questionDtos.get(0),questionDtos.get(1),questionDtos.get(2));

        //when
        List<QuestionDto> result = quizService.getQuizQuestionDtosById(QUIZ_ID);

        //then
        verify(quizRepository, times(1)).findById(anyString());
        verify(questionService, times(3)).getQuestionById(anyString());
        verify(modelMapper, times(3)).map(any(Question.class), any(Class.class));
        assertNotNull(result);
        int expectedSize = SAVED_PUBLIC_QUIZ.getQuestionIds().size();
        assertEquals(expectedSize, result.size());
        for (int i=0; i<expectedSize; i++) {
            assertEquals(SAVED_PUBLIC_QUIZ.getQuestionIds().get(i), result.get(i).getId());
            int expectedBadAnswersSize = questions.get(i).getAnswers().size()-1;
            assertEquals(expectedBadAnswersSize, result.get(i).getBadAnswers().size());
        }
    }

    @Test
    public void shouldGetAllPublicQuizzes() {
        //given
        when(quizRepository.findAll()).thenReturn(ALL_QUIZZES);

        //when
        List<Quiz> result = quizService.getAllPublicQuizzes();

        //then
        verify(quizRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        result.forEach(quiz -> assertTrue(quiz.getIsPublic()));
    }

    @Test
    public void shouldGetAllUserQuizzes() {
        //given
        when(userFinderService.findUserByUsername(anyString())).thenReturn(ENABLED_USER);
        when(quizRepository.findAll()).thenReturn(ALL_QUIZZES);

        //when
        List<Quiz> result = quizService.getAllUserQuizzes(UNIQUE_USERNAME);

        //then
        verify(userFinderService, times(1)).findUserByUsername(anyString());
        verify(quizRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(quiz -> assertEquals(ENABLED_USER.getId(), quiz.getAuthorId()));
    }

    @Test
    public void shouldUpdateQuiz() {
        //given
        Quiz quizUpdate = Quiz.builder()
                              .id(QUIZ_ID)
                              .title("updated title")
                              .category("test category")
                              .level(DifficultyLevel.EASY)
                              .isPublic(true)
                              .questionIds(List.of("q-123", "q-456", "q-789"))
                              .authorId("userId-098")
                              .build();
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(SAVED_PUBLIC_QUIZ));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quizUpdate);
        //when
        Quiz result = quizService.updateQuiz(QUIZ_ID, quizUpdate);

        //then
        verify(quizRepository, times(1)).findById(anyString());
        verify(quizRepository, times(1)).save(any(Quiz.class));
        assertNotNull(result);
        assertEquals(quizUpdate.getTitle(), result.getTitle());
    }

    @Test
    public void shouldDeleteQuiz() {
        //given
        List<Question> questions = createQuestions(SAVED_PUBLIC_QUIZ.getQuestionIds());
        when(quizRepository.existsById(QUIZ_ID)).thenReturn(true);
        when(quizRepository.findById(QUIZ_ID)).thenReturn(Optional.of(SAVED_PUBLIC_QUIZ));
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0),questions.get(1),questions.get(2));

        //when
        String result = quizService.deleteQuiz(QUIZ_ID);

        //then
        verify(quizRepository, times(1)).existsById(anyString());
        verify(quizRepository, times(1)).deleteById(anyString());
        verify(questionService, times(3)).getQuestionById(anyString());
        assertNotNull(result);
        assertEquals("Quiz successfully deleted", result);
    }

    private List<Question> createQuestions(List<String> ids) {
        return ids
            .stream()
            .map(id -> Question.builder()
                       .id(id)
                       .question("question"+id)
                       .answers(List.of("a","b", "c"))
                       .correctAnswer("a")
                       .build())
            .collect(Collectors.toList());
    }
}