package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.CustomUser;

public interface UserFinderService {
    CustomUser findUserByUsername(String username);
}
