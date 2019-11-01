package com.springboot.rest.quizmania.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    private final JavaMailSender sender;

    public EmailSenderService(JavaMailSender sender) {
        this.sender = sender;
    }

    public JavaMailSender getSender() {
        return sender;
    }

    @Async
    public void sendEmail(MimeMessage email) {
        sender.send(email);
    }
}
