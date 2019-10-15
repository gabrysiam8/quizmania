package com.springboot.rest.quizmania.domain;

import java.util.Map;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="score")
@Data
public class Score {

    @NotBlank
    private String quizId;

    private String userId;

    private Map<@NotBlank String,String> userAnswers;

    private double percentageScore;
}
