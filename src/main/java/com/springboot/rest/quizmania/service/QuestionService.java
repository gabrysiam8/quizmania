package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.dto.QuestionDto;

public interface QuestionService {

    Question addQuestion(QuestionDto questionDto);
    Question getQuestionById(String id);
    Question updateQuestion(String id, QuestionDto newQuestionDto);
    String deleteQuestion(String id);
}
