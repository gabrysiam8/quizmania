package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, ModelMapper mapper) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public CustomUser findUserByUsername(String username) {
        CustomUser currentUser = repository.findByUsername(username);
        if(currentUser==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");
        return currentUser;
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
}
