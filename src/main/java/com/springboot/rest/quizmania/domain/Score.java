package com.springboot.rest.quizmania.domain;

import java.util.Date;
import java.util.Map;
import javax.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="score")
@Data
@Builder
public class Score {

    @Id
    private String id;

    @NotBlank
    private String quizId;

    private String userId;

    private Date startDate;

    private Date endDate;

    private long elapsedTimeInMs;

    private Map<@NotBlank String,String> userAnswers;

    private int goodAnswers;

    private int allAnswers;

    private double percentageScore;
}
