package com.springboot.rest.quizmania.controller;

import javax.validation.Valid;

import com.springboot.rest.quizmania.domain.Quiz;
import com.springboot.rest.quizmania.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService service;

    public QuizController(QuizService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addQuiz(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Quiz quiz) {
        Quiz newQuiz =  service.addQuiz(userDetails, quiz);
        return new ResponseEntity<>(newQuiz, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPublicQuizzes() {
        return new ResponseEntity<>(service.getAllPublicQuizzes(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllUserQuizzes(@AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(service.getAllUserQuizzes(userDetails), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable(value="id") String id) {
        try {
            return new ResponseEntity<>(service.getQuizById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable(value="id") String id, @Valid @RequestBody Quiz quiz) {
        try {
            return new ResponseEntity<>(service.updateQuiz(id, quiz), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable(value="id") String id) {
        try {
            String msg = service.deleteQuiz(id);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/level")
    public ResponseEntity<?> getAllQuizLevels() {
        return new ResponseEntity<>(service.getQuizDifficultyLevels(), HttpStatus.OK);
    }
}
