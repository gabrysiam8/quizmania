package com.springboot.rest.quizmania.controller;

import javax.validation.Valid;

import com.springboot.rest.quizmania.domain.Score;
import com.springboot.rest.quizmania.service.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/score")
public class ScoreController {

    private final ScoreService service;

    public ScoreController(ScoreService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> addScore(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody Score score) {
        try {
            Score newScore = service.addScore(userDetails, score);
            return new ResponseEntity<>(newScore, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScoreById(@PathVariable(value="id") String id) {
        try {
            return new ResponseEntity<>(service.getScoreById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
