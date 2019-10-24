package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final ModelMapper mapper;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSenderService emailSenderService;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, ModelMapper mapper,
                       ConfirmationTokenService confirmationTokenService, EmailSenderService emailSenderService) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
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

    public CustomUser registerUser(CustomUser user) {
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

        String link = "https://quizmania-api.herokuapp.com/api/confirmation?token="+confirmationToken.getToken();
        String content = "To confirm your account, please click here: <a href="+link+">verify</a>";

        MimeMessage mail = emailSenderService.getSender().createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mail, true);
            helper.setTo(user.getEmail());
            helper.setReplyTo("quizmania@no-reply.com");
            helper.setFrom("quizmania@no-reply.com");
            helper.setSubject("Complete Registration");
            helper.setText(content, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        emailSenderService.sendEmail(mail);

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

//    public Map<String,String> refreshAndGetNewToken(String token) {
//        String username = tokenProvider.getUsernameFromToken(token);
//        CustomUser user = repository.findByUsername(username);
//        if(tokenProvider.canTokenBeRefreshed(token)) {
//            Map<String,String> response = new HashMap<>();
//            response.put("tokenType", "Bearer");
//            response.put("token", tokenProvider.refreshToken(token));
//            return response;
//        }
//        return null;
//    }

    public UserDto getUserInfo(UserDetails userDetails) {
        CustomUser user = repository.findByUsername(userDetails.getUsername());
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        mapper.map(user, UserDto.class);

        return mapper.map(user, UserDto.class);
    }

    public List<UserDto> getAllUsersInfo() {
        return repository
            .findAll()
            .stream()
            .map(user -> mapper.map(user, UserDto.class))
            .collect(Collectors.toList());
    }

    public String updateUserPassword(UserDetails currentUser, PasswordDto passwords) {
        CustomUser userUpdate = repository.findByUsername(currentUser.getUsername());
        if(userUpdate==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        if(!passwordEncoder.matches(passwords.getOldPassword(), userUpdate.getPassword()))
            throw new IllegalArgumentException("Wrong password!");
        if(!passwords.getNewPassword().equals(passwords.getPasswordConfirmation()))
            throw new IllegalArgumentException("The Password confirmation must match New password");
        userUpdate.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        repository.save(userUpdate);
        return "Password successfully changed";
    }

    public String deleteUser(UserDetails currentUser) {
        CustomUser user = repository.findByUsername(currentUser.getUsername());
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        repository.delete(user);
        return "Account successfully deleted";
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
        this.enableUser(confirmationToken.getUser());
        return "Account successfully verified.";
    }
}
