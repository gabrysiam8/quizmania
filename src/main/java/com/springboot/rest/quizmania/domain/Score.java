package com.springboot.rest.quizmania.domain;

import java.util.Map;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="score")
@Data
public class Score {

    @Id
    private String id;

    @NotBlank
    private String quizId;

    private String userId;

    private Map<@NotBlank String,String> userAnswers;

    private int goodAnswers;

    private int allAnswers;

    private double percentageScore;
}
