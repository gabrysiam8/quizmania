package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final QuizService quizService;

    private final ScoreService scoreService;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder, QuizService quizService,
                       ScoreService scoreService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.quizService = quizService;
        this.scoreService = scoreService;
    }

    public UserDto getUserInfo(String username) {
        CustomUser user = repository.findByUsername(username);
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());

        int quizAdded = quizService.getAllUserQuizzes(username).size();
        userDto.setQuizAddedNumber(quizAdded);

        int quizAttempts = scoreService.getScoresByUser(username).size();
        userDto.setQuizAttemptsNumber(quizAttempts);

        return userDto;
    }

    public String updateUserPassword(String username, PasswordDto passwords) {
        CustomUser userUpdate = repository.findByUsername(username);
        if(userUpdate==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        if(!passwordEncoder.matches(passwords.getOldPassword(), userUpdate.getPassword()))
            throw new IllegalArgumentException("Wrong password!");
        if(!passwords.getNewPassword().equals(passwords.getPasswordConfirmation()))
            throw new IllegalArgumentException("The Password confirmation must match New password!");
        userUpdate.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        repository.save(userUpdate);
        return "Password successfully changed";
    }

    public String resetUserPassword(String id, PasswordDto passwords) {
        CustomUser userUpdate = repository
            .findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("No user with id exists!"));

        if(!passwords.getNewPassword().equals(passwords.getPasswordConfirmation()))
            throw new IllegalArgumentException("The Password confirmation must match New password!");
        userUpdate.setPassword(passwordEncoder.encode(passwords.getNewPassword()));
        repository.save(userUpdate);
        return "Password successfully changed";
    }

    public String deleteUser(String username) {
        CustomUser user = repository.findByUsername(username);
        if(user==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");

        repository.delete(user);
        return "Account successfully deleted";
    }
}
