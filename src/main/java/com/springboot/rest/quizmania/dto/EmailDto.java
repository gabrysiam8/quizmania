package com.springboot.rest.quizmania.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    private String to;

    private String replyTo;

    private String from;

    private String subject;

    private String content;
}
