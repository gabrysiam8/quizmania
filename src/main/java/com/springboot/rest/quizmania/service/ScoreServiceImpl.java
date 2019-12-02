package com.springboot.rest.quizmania.service;

import java.util.List;
import java.util.stream.IntStream;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.repository.ScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class ScoreServiceImpl implements ScoreService {

    private final ScoreRepository repository;

    private final UserFinderService userFinderService;

    private final QuestionService questionService;

    public ScoreServiceImpl(ScoreRepository repository, UserFinderService userFinderService, QuestionService questionService) {
        this.repository = repository;
        this.userFinderService = userFinderService;
        this.questionService = questionService;
    }

    @Override
    public Score addScore(String username, Score score) {
        if(username != null) {
            CustomUser currentUser = userFinderService.findUserByUsername(username);
            score.setUserId(currentUser.getId());
        }

        score.setElapsedTimeInMs(score.getEndDate().getTime()-score.getStartDate().getTime());

        IntStream result = score
            .getUserAnswers()
            .entrySet()
            .stream()
            .mapToInt(entry -> {
                Question question = questionService.getQuestionById(entry.getKey());
                String correctAnswer = question.getCorrectAnswer();
                return correctAnswer.equals(entry.getValue()) ? 1 : 0;
            });

        int goodAnswers = result.sum();
        score.setGoodAnswers(goodAnswers);
        int allAnswers = score.getUserAnswers().size();
        score.setAllAnswers(allAnswers);
        double avg = (double)goodAnswers/allAnswers;
        score.setPercentageScore(Math.round(avg*10000.0)/100.0);

        return repository.save(score);
    }

    @Override
    public Score getScoreById(String id) {
        return repository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No score with that id exists!"));
    }

    @Override
    public List<Score> getScoresByUser(String username) {
        CustomUser currentUser = userFinderService.findUserByUsername(username);
        return repository.getScoresByUserId(currentUser.getId());
    }

    @Override
    public List<Score> getScoresByQuizId(String quizId) {
        return repository.getScoresByQuizId(quizId);
    }
}
