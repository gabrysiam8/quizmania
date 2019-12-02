package com.springboot.rest.quizmania.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService, UserFinderService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUser user = Optional.ofNullable(repository.findByEmail(username)).orElse(repository.findByUsername(username));
        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        String role = user.getRole();
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        return new User(user.getUsername(), user.getPassword(), authorities);
    }

    @Override
    public CustomUser findUserByUsername(String username) {
        CustomUser currentUser = repository.findByUsername(username);
        if(currentUser==null)
            throw new UsernameNotFoundException("No user with that email or username exists!");
        return currentUser;
    }
}
