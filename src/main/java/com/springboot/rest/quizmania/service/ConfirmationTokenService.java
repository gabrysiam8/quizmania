package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;

public interface ConfirmationTokenService {

    ConfirmationToken createToken(CustomUser user);
    ConfirmationToken getConfirmationToken(String token);
    ConfirmationToken confirmToken(String token);
}
