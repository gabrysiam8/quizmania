package com.springboot.rest.quizmania.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    public QuestionService(QuestionRepository repository) {
        this.repository = repository;
    }

    public Question addQuestion(QuestionDto questionDto) {
        Question newQuestion = createQuestionFromQuestionDto(questionDto);
        return repository.save(newQuestion);
    }

    public Question getQuestionById(String id) {
        return repository
                    .findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("No question with that id exists!"));
    }

    public Question updateQuestion(String id, QuestionDto newQuestionDto) {
        Question questionUpdate = repository
                                    .findById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("No question with that id exists!"));

        Question newQuestion = createQuestionFromQuestionDto(newQuestionDto);

        if(newQuestion.equals(questionUpdate))
            return questionUpdate;

        return repository.save(newQuestion);
    }

    public String deleteQuestion(String id) {
        if(!repository.existsById(id))
            throw new IllegalArgumentException("No question with that id exists!");
        repository.deleteById(id);
        return "Question successfully deleted";
    }

    private Question createQuestionFromQuestionDto(QuestionDto questionDto) {
        List<String> allAnswers = new ArrayList<>(questionDto.getBadAnswers());
        allAnswers.add(questionDto.getCorrectAnswer());
        Collections.shuffle(allAnswers);

       return Question.builder()
                      .id(questionDto.getId())
                      .question(questionDto.getQuestion())
                      .answers(allAnswers)
                      .correctAnswer(questionDto.getCorrectAnswer())
                      .build();
    }
}
