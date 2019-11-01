package com.springboot.rest.quizmania.service;

import java.util.UUID;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository repository;

    public ConfirmationTokenService(ConfirmationTokenRepository repository) {
        this.repository = repository;
    }

    public ConfirmationToken createToken(CustomUser user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, user);
        return repository.save(confirmationToken);
    }

    public ConfirmationToken getConfirmationToken(String token) {
        return repository.findByToken(token);
    }
}
