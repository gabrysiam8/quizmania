package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.stream.Collectors;

import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.repository.QuizRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    private final QuizRepository repository;

    public QuizService(QuizRepository repository) {
        this.repository = repository;
    }

    public Quiz addQuiz(Quiz quiz) {
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
}
