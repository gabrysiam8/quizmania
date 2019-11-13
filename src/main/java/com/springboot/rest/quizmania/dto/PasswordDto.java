package com.springboot.rest.quizmania.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordDto {

    private String oldPassword;

    @NotBlank
    private String newPassword;

    @NotBlank
    private String passwordConfirmation;
}
