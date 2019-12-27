package com.springboot.rest.quizmania.service;

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
import com.springboot.rest.quizmania.dto.UserRegisterDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSenderService emailSenderService;

    private final static String APP_URL = "https://quizmania-app.herokuapp.com";


    public AuthServiceImpl(UserRepository repository, AuthenticationManager authenticationManager,
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

    private CustomUser enableUser(CustomUser user) {
        user.setEnabled(true);
        return repository.save(user);
    }

    @Override
    public CustomUser registerUser(UserRegisterDto user) throws MessagingException {
        if(repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists!");
        }
        if(repository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("User with this username already exists!");
        }
        if(!user.getPassword().equals(user.getPasswordConfirmation())) {
            throw new IllegalArgumentException("The Password confirmation must match Password!");
        }

        CustomUser newUser = CustomUser.builder()
                                       .email(user.getEmail())
                                       .username(user.getUsername())
                                       .password(passwordEncoder.encode(user.getPassword()))
                                       .role("USER")
                                       .build();
        CustomUser savedUser = repository.save(newUser);

        ConfirmationToken confirmationToken = confirmationTokenService.createToken(savedUser);
        String link = APP_URL+"/confirmation?token="+confirmationToken.getToken();
        String content = "To confirm your account, please click here: <a href="+link+">verify</a>";

        EmailDto emailDto = createEmailMessage(user.getEmail(), "Complete Registration", content);

        try {
            MimeMessage mail = emailSenderService.createMimeMessage(emailDto);
            emailSenderService.sendEmail(mail);
        } catch (MessagingException | MailSendException e) {
            repository.delete(savedUser);
            throw e;
        }

        return savedUser;
    }

    @Override
    public Map<String,String> loginUser(UserLoginDto userDto) {
        CustomUser user = findUserByEmailOrUsername(userDto.getUsername());
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                userDto.getPassword()
            )
        );

        if(!user.isEnabled())
            throw new DisabledException("User account is locked!");

        Map<String,String> response = new HashMap<>();
        response.put("tokenType", "Bearer");
        response.put("token", tokenProvider.generateToken(user));

        return response;
    }

    @Override
    public String confirmUserAccount(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.confirmToken(token);
        enableUser(confirmationToken.getUser());
        return "Account successfully verified.";
    }

    @Override
    public String sendResetPasswordEmail(String email) throws MessagingException {
        CustomUser user = repository.findByEmail(email);
        if(user==null) {
            throw new IllegalArgumentException("User with that email not exists!");
        }

        ConfirmationToken confirmationToken = confirmationTokenService.createToken(user);
        String link = APP_URL+"/resetPassword?token="+confirmationToken.getToken();
        String content = "To reset your password, please click here: <a href="+link+">reset</a>";

        EmailDto emailDto = createEmailMessage(email, "Reset password", content);

        MimeMessage mail = emailSenderService.createMimeMessage(emailDto);
        emailSenderService.sendEmail(mail);

        return "Email successfully send";
    }

    private EmailDto createEmailMessage(String emailAddress, String subject, String content) {
        return EmailDto.builder()
                       .to(emailAddress)
                       .replyTo("quizmania@no-reply.com")
                       .from("quizmania@no-reply.com")
                       .subject(subject)
                       .content(content)
                       .build();
    }
}
