package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.repository.QuizRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuizServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private AuthService authService;

    @Mock
    private QuestionService questionService;

    private QuizService quizService;

    private Quiz quiz;

    private Quiz savedQuiz;

    private List<Quiz> allQuizzes;

    private CustomUser author;

    private static final String UNIQUE_USERNAME = "test";

    private static final String QUIZ_ID = "testId-1234";

    @Before
    public void setUp() {
        quizService = new QuizService(quizRepository, authService, questionService);

        quiz = Quiz.builder()
            .title("test title")
            .category("test category")
            .level(DifficultyLevel.EASY)
            .questionIds(List.of("q-123", "q-456", "q-789"))
            .build();

        savedQuiz = Quiz.builder()
                   .id(QUIZ_ID)
                   .title("test title")
                   .category("test category")
                   .level(DifficultyLevel.EASY)
                   .isPublic(true)
                   .questionIds(List.of("q-123", "q-456", "q-789"))
                   .authorId("userId-098")
                   .build();

        Quiz privateQuiz = Quiz.builder()
                        .id("testId-2345")
                        .title("another test title")
                        .category("test category")
                        .level(DifficultyLevel.EASY)
                        .isPublic(false)
                        .questionIds(List.of("q-321", "q-654"))
                        .authorId("userId-098")
                        .build();

        allQuizzes = List.of(savedQuiz, privateQuiz);

        author = CustomUser.builder()
                           .id("userId-098")
                           .email("test@gmail.com")
                           .username(UNIQUE_USERNAME)
                           .password("pass")
                           .enabled(true)
                           .build();
    }

    @Test
    public void shouldAddQuiz() {
        //given
        when(authService.findUserByUsername(anyString())).thenReturn(author);
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);

        //when
        Quiz result = quizService.addQuiz(UNIQUE_USERNAME, quiz);

        //then
        verify(authService, times(1)).findUserByUsername(anyString());
        verify(quizRepository, times(1)).save(any(Quiz.class));
        assertNotNull(result);
        assertEquals(QUIZ_ID, result.getId());
        assertEquals(author.getId(), result.getAuthorId());
    }

    @Test
    public void shouldGetQuizByIdWithoutFilteringWhenNoFieldGiven() throws JsonProcessingException {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(savedQuiz));

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
            + "\"authorId\":\"userId-098\"}";
        assertEquals(expected, result);
    }

    @Test
    public void shouldGetQuizByIdWithFilteringWhenTitleFieldGiven() throws JsonProcessingException {
        //given
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(savedQuiz));

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
        when(quizRepository.findById(anyString())).thenReturn(Optional.ofNullable(savedQuiz));
        List<Question> questions = createQuestions(savedQuiz.getQuestionIds());
        when(questionService.getQuestionById(anyString())).thenReturn(questions.get(0),questions.get(1),questions.get(2));

        //when
        List<Question> result = quizService.getQuizQuestionsById(QUIZ_ID);

        //then
        verify(quizRepository, times(1)).findById(anyString());
        verify(questionService, times(3)).getQuestionById(anyString());
        assertNotNull(result);
        int expectedSize = savedQuiz.getQuestionIds().size();
        assertEquals(expectedSize, result.size());
        for (int i=0; i<expectedSize; i++)
            assertEquals(savedQuiz.getQuestionIds().get(i), result.get(i).getId());
    }

    @Test
    public void shouldGetAllPublicQuizzes() {
        //given
        when(quizRepository.findAll()).thenReturn(allQuizzes);

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
        when(authService.findUserByUsername(anyString())).thenReturn(author);
        when(quizRepository.findAll()).thenReturn(allQuizzes);

        //when
        List<Quiz> result = quizService.getAllUserQuizzes(UNIQUE_USERNAME);

        //then
        verify(authService, times(1)).findUserByUsername(anyString());
        verify(quizRepository, times(1)).findAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        result.forEach(quiz -> assertEquals(author.getId(), quiz.getAuthorId()));
    }

    @Test
    public void shouldUpdateQuiz() {
        //given
        Quiz quizUpdate = Quiz.builder()
                              .title("updated title")
                              .category("test category")
                              .level(DifficultyLevel.EASY)
                              .isPublic(true)
                              .questionIds(List.of("q-123", "q-456", "q-789"))
                              .authorId("userId-098")
                              .build();
        when(quizRepository.findById(QUIZ_ID)).thenReturn(Optional.ofNullable(savedQuiz));
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
        when(quizRepository.existsById(QUIZ_ID)).thenReturn(true);

        //when
        String result = quizService.deleteQuiz(QUIZ_ID);

        //then
        verify(quizRepository, times(1)).existsById(anyString());
        verify(quizRepository, times(1)).deleteById(anyString());
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