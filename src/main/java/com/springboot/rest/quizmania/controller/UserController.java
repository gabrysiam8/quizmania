package com.springboot.rest.quizmania.controller;

import javax.validation.Valid;

import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserDto userDto = service.getUserInfo(userDetails.getUsername());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/me/password")
    public ResponseEntity<?> updateCurrentUserPassword(@AuthenticationPrincipal UserDetails currentUser, @Valid @RequestBody PasswordDto passwords) {
        try {
            String msg = service.updateUserPassword(currentUser.getUsername(), passwords);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<?> resetUserPassword(@PathVariable(value="id") String id, @Valid @RequestBody PasswordDto passwords) {
        try {
            String msg = service.resetUserPassword(id, passwords);
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        String msg = service.deleteUser(currentUser.getUsername());
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

}
