package com.springboot.rest.quizmania.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider tokenProvider;

    private final ModelMapper mapper;

    public UserService(UserRepository repository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, ModelMapper mapper) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.mapper = mapper;
    }

    private CustomUser findUserByEmailOrUsername(String username) {
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
        Map<String,String> response = new HashMap<>();
        response.put("tokenType", "Bearer");
        response.put("token", tokenProvider.generateToken(user));

        return response;
    }

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

    public String updateUserPassword(UserDetails currentUser, String oldPassword, String newPassword) {
        CustomUser userUpdate = repository.findByUsername(currentUser.getUsername());
        if(userUpdate==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        if(!passwordEncoder.matches(oldPassword, userUpdate.getPassword()))
            throw new IllegalArgumentException("Wrong password!");

        userUpdate.setPassword(passwordEncoder.encode(newPassword));
        return "Password changed successfully";
    }

    public String deleteUser(UserDetails currentUser) {
        CustomUser user = repository.findByUsername(currentUser.getUsername());
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        repository.delete(user);
        return "Account deleted successfully";
    }
}
