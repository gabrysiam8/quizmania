package com.springboot.rest.quizmania.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.repository.QuizRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final QuizRepository repository;

    private final AuthService authService;

    private final QuestionService questionService;

    public QuizService(QuizRepository repository, AuthService authService, QuestionService questionService) {
        this.repository = repository;
        this.authService = authService;
        this.questionService = questionService;
    }

    public Quiz addQuiz(String username, Quiz quiz) {
        CustomUser currentUser = authService.findUserByUsername(username);
        quiz.setAuthorId(currentUser.getId());
        return repository.save(quiz);
    }

    public String getQuizById(String id, String[] fields) throws JsonProcessingException {
        Quiz quiz = repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No quiz with that id exists!"));

        ObjectMapper mapper = new ObjectMapper();

        SimpleBeanPropertyFilter theFilter = fields==null ? SimpleBeanPropertyFilter.serializeAll() : SimpleBeanPropertyFilter.filterOutAllExcept(fields);
        FilterProvider filters = new SimpleFilterProvider().addFilter("quizFilter", theFilter);

        return mapper.writer(filters).writeValueAsString(quiz);
    }

    public List<Question> getQuizQuestionsById(String id) {
        return repository
                .findById(id)
                .map(quiz -> quiz
                        .getQuestionIds()
                        .stream()
                        .map(questionService::getQuestionById)
                        .collect(Collectors.toList())
                )
                .orElseThrow(() -> new IllegalArgumentException("No quiz with that id exists!"));
    }

    public List<Quiz> getAllPublicQuizzes() {
        return repository
            .findAll()
            .stream()
            .filter(Quiz::getIsPublic)
            .collect(Collectors.toList());
    }

    public List<Quiz> getAllUserQuizzes(String username) {
        CustomUser currentUser = authService.findUserByUsername(username);
        return repository
            .findAll()
            .stream()
            .filter(quiz -> quiz.getAuthorId().equals(currentUser.getId()))
            .collect(Collectors.toList());
    }

    public Quiz updateQuiz(String id, Quiz newQuiz) {
        Quiz quizUpdate = repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No quiz with that id exists!"));
        if(newQuiz.equals(quizUpdate))
            return quizUpdate;

        newQuiz.setId(quizUpdate.getId());

        return repository.save(newQuiz);
    }

    public String deleteQuiz(String id) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No question with that id exists!");
        repository.deleteById(id);
        return "Quiz successfully deleted";
    }

    public List<String> getQuizDifficultyLevels() {
        return Arrays
            .stream(DifficultyLevel.values())
            .map(Enum::toString)
            .collect(Collectors.toList());
    }
}
