package com.springboot.rest.quizmania.controller;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager, UserRepository repository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody CustomUser user) {
        if(repository.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>("User with this email already exists!", HttpStatus.CONFLICT);
        }
        if(repository.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>("User with this username already exists!", HttpStatus.CONFLICT);
        }

        CustomUser newUser = CustomUser.builder()
            .email(user.getEmail())
            .username(user.getUsername())
            .password(passwordEncoder.encode(user.getPassword()))
            .role("USER")
            .build();

        CustomUser result = repository.save(newUser);

        return new ResponseEntity<>("Account successfully created", HttpStatus.CREATED);
    }
}
