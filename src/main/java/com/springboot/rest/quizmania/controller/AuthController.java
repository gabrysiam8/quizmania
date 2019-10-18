package com.springboot.rest.quizmania.controller;

import java.util.Calendar;
import javax.mail.MessagingException;
import javax.validation.Valid;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.service.ConfirmationTokenService;
import com.springboot.rest.quizmania.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService service;

    private final ConfirmationTokenService confirmationTokenService;

    public AuthController(UserService service, ConfirmationTokenService confirmationTokenService) {
        this.service = service;
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CustomUser user) {
        try {
            service.registerUser(user);
            return new ResponseEntity<>("Account successfully created", HttpStatus.CREATED);
        } catch(IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDto userLoginDto) {
        try {
            return new ResponseEntity<>(service.loginUser(userLoginDto), HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value="/confirmation")
    public ResponseEntity<?> confirmUserAccount(@RequestParam("token")String confirmationToken)
    {   try {
            String message = service.confirmUserAccount(confirmationToken);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


}
