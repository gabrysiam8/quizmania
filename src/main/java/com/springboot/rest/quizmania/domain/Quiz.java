package com.springboot.rest.quizmania.domain;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="quiz")
@Data
public class Quiz {

    private List<Question> questions;

    private String category;

    private DifficultyLevel level;
}
