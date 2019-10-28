package com.springboot.rest.quizmania.controller;

import com.springboot.rest.quizmania.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService service;

    public StatisticsController(StatisticsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getQuizById(@RequestParam(value = "quizId", required = true) String quizId) {
        try {
            return new ResponseEntity<>(service.getQuizStatisticsById(quizId), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
