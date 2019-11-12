package com.springboot.rest.quizmania.controller;

import javax.validation.Valid;

import com.springboot.rest.quizmania.domain.Question;
import com.springboot.rest.quizmania.dto.QuestionDto;
import com.springboot.rest.quizmania.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService service;

    public QuestionController(QuestionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addQuestion(@Valid @RequestBody QuestionDto questionDto) {
        Question newQuestion = service.addQuestion(questionDto);
        return new ResponseEntity<>(newQuestion, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable(value="id") String id) {
        try {
            return new ResponseEntity<>(service.getQuestionById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable(value="id") String id, @Valid @RequestBody QuestionDto questionDto) {
        try {
            return new ResponseEntity<>(service.updateQuestion(id, questionDto), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable(value="id") String id) {
        try {
            String msg = service.deleteQuestion(id);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
