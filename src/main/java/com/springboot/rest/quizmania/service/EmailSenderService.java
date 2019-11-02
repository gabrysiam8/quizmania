package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.dto.EmailDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSenderService {

    private final JavaMailSender sender;

    public EmailSenderService(JavaMailSender sender) {
        this.sender = sender;
    }

    public MimeMessage createMimeMessage(EmailDto emailDto) throws MessagingException {
        MimeMessage mail = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(emailDto.getTo());
        helper.setReplyTo(emailDto.getReplyTo());
        helper.setFrom(emailDto.getFrom());
        helper.setSubject(emailDto.getSubject());
        helper.setText(emailDto.getContent(), true);
        return mail;
    }

    @Async
    public void sendEmail(MimeMessage email) {
        sender.send(email);
    }
}
