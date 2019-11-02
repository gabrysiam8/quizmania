package com.springboot.rest.quizmania.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.EmailDto;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSenderService emailSenderService;


    public AuthService(UserRepository repository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider,
                       ConfirmationTokenService confirmationTokenService, EmailSenderService emailSenderService) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderService = emailSenderService;
    }

    private CustomUser findUserByEmailOrUsername(String emailOrUsername) {
        return Optional
            .ofNullable(repository.findByEmail(emailOrUsername))
            .orElse(repository.findByUsername(emailOrUsername));
    }

    public CustomUser findUserByUsername(String username) {
        CustomUser currentUser = repository.findByUsername(username);
        if(currentUser==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");
        return currentUser;
    }

    private CustomUser enableUser(CustomUser user) {
        user.setEnabled(true);
        return repository.save(user);
    }

    public CustomUser registerUser(CustomUser user) throws MessagingException {
        if(repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists!");
        }
        if(repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User with this username already exists!");
        }

        CustomUser newUser = CustomUser.builder()
                                       .email(user.getEmail())
                                       .username(user.getUsername())
                                       .password(passwordEncoder.encode(user.getPassword()))
                                       .role("USER")
                                       .build();
        CustomUser savedUser = repository.save(newUser);

        ConfirmationToken confirmationToken = confirmationTokenService.createToken(savedUser);
        String link = "https://quizmania-app.herokuapp.com/confirmation?token="+confirmationToken.getToken();
        String content = "To confirm your account, please click here: <a href="+link+">verify</a>";

        EmailDto emailDto = EmailDto.builder()
            .to(user.getEmail())
            .replyTo("quizmania@no-reply.com")
            .from("quizmania@no-reply.com")
            .subject("Complete Registration")
            .content(content)
            .build();

        try {
            MimeMessage mail = emailSenderService.createMimeMessage(emailDto);
            emailSenderService.sendEmail(mail);
        } catch (MessagingException e) {
            repository.delete(savedUser);
            throw e;
        }

        return savedUser;
    }

    public Map<String,String> loginUser(UserLoginDto userDto) {
        CustomUser user = findUserByEmailOrUsername(userDto.getUsername());
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                userDto.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        if(!user.isEnabled())
            throw new DisabledException("User account is locked!");

        Map<String,String> response = new HashMap<>();
        response.put("tokenType", "Bearer");
        response.put("token", tokenProvider.generateToken(user));

        return response;
    }

    public String confirmUserAccount(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        if(confirmationToken==null) {
            throw new IllegalArgumentException("Invalid token.");
        }
        Calendar cal = Calendar.getInstance();
        if ((confirmationToken.getExpirationDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Token have expired.");
        }
        enableUser(confirmationToken.getUser());
        return "Account successfully verified.";
    }
}
