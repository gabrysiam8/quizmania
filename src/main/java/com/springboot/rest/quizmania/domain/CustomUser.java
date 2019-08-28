package com.springboot.rest.quizmania.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="user")
@Data
public class CustomUser {

    @Id
    private String id;

    @Indexed(name = "email_index", direction = IndexDirection.DESCENDING)
    private String email;

    private String username;
    private String password;
    private boolean isAdmin;
}
