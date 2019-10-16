package com.springboot.rest.quizmania.domain;

import java.util.Calendar;
import java.util.Date;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="token")
@Data
public class ConfirmationToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    private String id;

    private String token;

    private Date expirationDate;

    private CustomUser user;

    public ConfirmationToken(String token, CustomUser user) {
        this.token = token;
        this.user = user;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, EXPIRATION);
        this.expirationDate = new Date(cal.getTime().getTime());
    }
}
