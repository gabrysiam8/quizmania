package com.springboot.rest.quizmania.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public CustomUser findUserByEmailOrUsername(String username) {
        return Optional
            .ofNullable(repository.findByEmail(username))
            .orElse(repository.findByUsername(username));
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

        return repository.save(newUser);
    }

    public Map<String,String> loginUser(UserDto userDto) {
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
        Map<String,String> result = new HashMap<>();
        result.put("tokenType", "Bearer");
        result.put("token", tokenProvider.generateToken(user));

        return result;
    }
}
