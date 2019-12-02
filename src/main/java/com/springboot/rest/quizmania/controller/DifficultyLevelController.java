package com.springboot.rest.quizmania.controller;

import com.springboot.rest.quizmania.service.DifficultyLevelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/level")
public class DifficultyLevelController {

    private final DifficultyLevelService service;

    public DifficultyLevelController(DifficultyLevelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> getAllDifficultyLevels() {
        return new ResponseEntity<>(service.getAllDifficultyLevels(), HttpStatus.OK);
    }
}
