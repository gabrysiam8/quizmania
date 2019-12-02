package com.springboot.rest.quizmania.controller;

import com.springboot.rest.quizmania.service.ConfirmationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/confirmation")
public class ConfirmationTokenController {

    private final ConfirmationTokenService service;

    public ConfirmationTokenController(ConfirmationTokenService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<?> confirmToken(@RequestParam("token")String token) {
        try {
            return new ResponseEntity<>(service.confirmToken(token), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
