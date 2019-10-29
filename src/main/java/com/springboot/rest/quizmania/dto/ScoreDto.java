package com.springboot.rest.quizmania.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreDto {

    private long elapsedTime;

    private double percentageScore;
}
