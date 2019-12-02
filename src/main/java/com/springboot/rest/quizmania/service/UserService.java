package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;

public interface UserService {
    UserDto getUserInfo(String username);
    String updateUserPassword(String username, PasswordDto passwords);
    String resetUserPassword(String id, PasswordDto passwords);
    String deleteUser(String username);
}
