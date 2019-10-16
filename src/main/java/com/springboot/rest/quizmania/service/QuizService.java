package com.springboot.rest.quizmania.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.DifficultyLevel;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.repository.QuizRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final QuizRepository repository;

    private final UserService userService;

    public QuizService(QuizRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    public Quiz addQuiz(UserDetails userDetails, Quiz quiz) {
        CustomUser currentUser = userService.findUserByUsername(userDetails.getUsername());
        quiz.setAuthorId(currentUser.getId());
        return repository.save(quiz);
    }

    public Quiz getQuizById(String id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No quiz with that id exists!"));
    }

    public List<Quiz> getAllPublicQuizzes() {
        return repository
            .findAll()
            .stream()
            .filter(Quiz::getIsPublic)
            .collect(Collectors.toList());
    }

    public List<Quiz> getAllUserQuizzes(UserDetails userDetails) {
        CustomUser currentUser = userService.findUserByUsername(userDetails.getUsername());
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
