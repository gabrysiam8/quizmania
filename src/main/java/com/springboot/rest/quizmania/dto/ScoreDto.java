package com.springboot.rest.quizmania.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreDto {

    private long elapsedTime;

    private double percentageScore;

    private Date startDate;
}
