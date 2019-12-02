package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.springboot.rest.quizmania.common.TestData.ENABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomUserDetailsServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @Before
    public void setUp() {
        customUserDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    public void shouldLoadUser() {
        //given
        when(userRepository.findByEmail(UNIQUE_USERNAME)).thenReturn(null);
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);

        //when
        UserDetails result = customUserDetailsService.loadUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
        assertNotNull(result);
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
        //given
        exception.expect(UsernameNotFoundException.class);
        exception.expectMessage("User not found");
        when(userRepository.findByEmail(UNIQUE_USERNAME)).thenReturn(null);
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(null);

        //when
        customUserDetailsService.loadUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).findByUsername(anyString());
    }
}