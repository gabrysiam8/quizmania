package com.springboot.rest.quizmania.domain;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="quiz")
@Data
public class Quiz {

    @Id
    private String id;

    @NotBlank
    private String category;

    private DifficultyLevel level;

    private Boolean isPublic;

    @Size(min=1)
    private List<@NotBlank String> questionIds;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quiz quiz = (Quiz) o;
        return Objects.equals(id, quiz.id) &&
            Objects.equals(category, quiz.category) &&
            level == quiz.level &&
            Objects.equals(isPublic, quiz.isPublic) &&
            Objects.equals(questionIds, quiz.questionIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, category, level, isPublic, questionIds);
    }
}
