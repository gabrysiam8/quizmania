package com.springboot.rest.quizmania.domain;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="question")
@Data
public class Question {

    @Id
    private String id;

    @NotBlank
    private String question;

    @Size(min=2, max=4)
    private List<@NotBlank String> answers;
}
