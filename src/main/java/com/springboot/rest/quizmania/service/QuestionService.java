package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question addQuestion(Question question) {
        return repository.save(question);
    }

    public Question getQuestionById(String id) {
        return repository
                    .findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("No question with that id exists!"));
    }

    public Question updateQuestion(String id, Question newQuestion) {
        Question questionUpdate = repository
                                    .findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("No question with that id exists!"));
        if(newQuestion.equals(questionUpdate))
            return questionUpdate;

        newQuestion.setId(questionUpdate.getId());

        return repository.save(newQuestion);
    }

    public String deleteQuestion(String id) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No question with that id exists!");
        repository.deleteById(id);
        return "Question successfully deleted";
    }
}
