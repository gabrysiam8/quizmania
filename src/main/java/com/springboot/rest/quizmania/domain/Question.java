package com.springboot.rest.quizmania.domain;

import java.util.List;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="question")
@Data
public class Question {

    private String question;

    private List<String> answers;
}
