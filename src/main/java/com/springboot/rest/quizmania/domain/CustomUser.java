package com.springboot.rest.quizmania.domain;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.fasterxml.jackson.annotation.JsonProperty.Access;

@Document(collection="user")
@Data
@Builder
public class CustomUser {

    @Id
    private String id;

    @NotBlank
    @Indexed(name = "email_index", direction = IndexDirection.DESCENDING)
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    private String role;
}
