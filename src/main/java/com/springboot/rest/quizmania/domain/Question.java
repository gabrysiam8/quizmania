package com.springboot.rest.quizmania.domain;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="question")
@Data
@Builder
public class Question {

    @Id
    private String id;

    @NotBlank
    private String question;

    @Size(min=2, max=4)
    private List<@NotBlank String> answers;

    @NotBlank
    private String correctAnswer;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question1 = (Question) o;
        return id.equals(question1.id) &&
            question.equals(question1.question) &&
            answers.equals(question1.answers) &&
            correctAnswer.equals(question1.correctAnswer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answers, correctAnswer);
    }
}
