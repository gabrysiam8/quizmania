package com.springboot.rest.quizmania.domain;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="quiz")
@Data
@Builder
@JsonFilter("quizFilter")
public class Quiz {

    @Id
    private String id;

    @NotBlank
    private String title;

    @NotBlank
    private String category;

    private String description;

    private DifficultyLevel level;

    private Boolean isPublic;

    @Size(min=1)
    private List<@NotBlank String> questionIds;

    private String authorId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quiz quiz = (Quiz) o;
        return id.equals(quiz.id) &&
            title.equals(quiz.title) &&
            category.equals(quiz.category) &&
            Objects.equals(description, quiz.description) &&
            level == quiz.level &&
            Objects.equals(isPublic, quiz.isPublic) &&
            questionIds.equals(quiz.questionIds) &&
            Objects.equals(authorId, quiz.authorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, description, level, isPublic, questionIds, authorId);
    }
}
