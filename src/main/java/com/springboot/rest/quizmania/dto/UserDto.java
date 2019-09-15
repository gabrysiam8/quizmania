package com.springboot.rest.quizmania.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserDto {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
