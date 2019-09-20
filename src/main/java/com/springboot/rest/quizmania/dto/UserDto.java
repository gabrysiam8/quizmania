package com.springboot.rest.quizmania.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonProperty.Access;

@Data
@NoArgsConstructor
public class UserDto {

    private String email;

    private String username;

    @JsonProperty(access = Access.READ_ONLY)
    private String role;
}
