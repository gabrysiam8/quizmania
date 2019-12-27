package com.springboot.rest.quizmania.service;

import java.util.Map;
import javax.mail.MessagingException;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.dto.UserRegisterDto;

public interface AuthService {

    CustomUser registerUser(UserRegisterDto user) throws MessagingException;
    Map<String,String> loginUser(UserLoginDto userDto);
    String confirmUserAccount(String token);
    String sendResetPasswordEmail(String email) throws MessagingException;
}
