package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.repository.ScoreRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ScoreService {

    private final ScoreRepository repository;

    private final UserService userService;

    private final QuestionService questionService;

    public ScoreService(ScoreRepository repository, UserService userService, QuestionService questionService) {
        this.repository = repository;
        this.userService = userService;
        this.questionService = questionService;
    }

    public Score addScore(UserDetails userDetails, Score score) {
        if(userDetails != null) {
            CustomUser currentUser = userService.findUserByUsername(userDetails.getUsername());
            score.setUserId(currentUser.getId());
        }
        score
            .getUserAnswers()
            .entrySet()
            .stream()
            .mapToInt(entry -> {
                Question question = questionService.getQuestionById(entry.getKey());
                String correctAnswer = question.getAnswers().get(question.getCorrectAnswer());
                return correctAnswer.equals(entry.getValue()) ? 1 : 0;
            })
            .average()
            .ifPresent(s -> score.setPercentageScore(Math.round(s*10000.0)/100.0));

        return repository.save(score);
    }
}
