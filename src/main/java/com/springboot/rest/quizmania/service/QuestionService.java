package com.springboot.rest.quizmania.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.repository.QuestionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository repository;

    private final ModelMapper modelMapper;

    public QuestionService(QuestionRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
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

        Question question = modelMapper.map(questionDto, Question.class);
        question.setAnswers(allAnswers);

       return question;
    }
}
