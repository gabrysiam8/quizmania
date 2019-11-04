package com.springboot.rest.quizmania.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;

    private String username;

    private int quizAddedNumber;

    private int quizAttemptsNumber;
}
