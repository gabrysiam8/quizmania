package com.springboot.rest.quizmania.dto;

import java.util.List;

import lombok.Data;

@Data
public class StatisticsDto {

    private int attemptsNumber;

    private double averageScore;

    private double bestScore;

    private double worstScore;

    private double averageTimeInMs;

    private List<Double> allPercentageScores;
}
