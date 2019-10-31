package com.springboot.rest.quizmania.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String email;

    private String username;

    private int quizAddedNumber;

    private int quizAttemptsNumber;
}
