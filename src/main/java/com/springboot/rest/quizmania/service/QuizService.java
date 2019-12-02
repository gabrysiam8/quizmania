package com.springboot.rest.quizmania.service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.dto.QuestionDto;

public interface QuizService {

    Quiz addQuiz(String username, Quiz quiz);
    String getQuizById(String id, String[] fields) throws JsonProcessingException;
    List<Question> getQuizQuestionsById(String id);
    List<QuestionDto> getQuizQuestionDtosById(String id);
    List<Quiz> getAllPublicQuizzes();
    List<Quiz> getAllUserQuizzes(String username);
    Quiz updateQuiz(String id, Quiz newQuiz);
    String deleteQuiz(String id);
}
