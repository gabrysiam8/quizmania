package com.springboot.rest.quizmania.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionDto {

    private String id;

    @NotBlank
    private String question;

    @Size(min=1, max=3)
    private List<@NotBlank String> badAnswers;

    @NotBlank
    private String correctAnswer;

}
