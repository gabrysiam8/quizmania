package com.springboot.rest.quizmania.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class PasswordDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
