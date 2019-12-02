package com.springboot.rest.quizmania.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.springboot.rest.quizmania.domain.DifficultyLevel;
import org.springframework.stereotype.Service;

@Service
public class DifficultyLevelServiceImpl implements DifficultyLevelService {

    @Override
    public List<String> getAllDifficultyLevels() {
        return Arrays
            .stream(DifficultyLevel.values())
            .map(Enum::toString)
            .collect(Collectors.toList());
    }
}
