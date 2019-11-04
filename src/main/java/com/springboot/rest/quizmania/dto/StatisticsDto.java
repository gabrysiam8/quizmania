package com.springboot.rest.quizmania.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {

    private int attemptsNumber;

    private double averageScore;

    private double bestScore;

    private double worstScore;

    private double averageTimeInMs;

    private List<ScoreDto> scoreDtoList;
}
