package com.springboot.rest.quizmania.service;

import java.util.Collections;
import java.util.Optional;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.PasswordDto;
import com.springboot.rest.quizmania.dto.UserDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.springboot.rest.quizmania.common.TestData.ENABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.USER_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private QuizService quizService;

    @Mock
    private ScoreService scoreService;

    private UserServiceImpl userServiceImpl;

    @Before
    public void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, quizService, scoreService);
    }

    @Test
    public void shouldReturnUserInfo() {
        //given
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(quizService.getAllUserQuizzes(UNIQUE_USERNAME)).thenReturn(Collections.emptyList());
        when(scoreService.getScoresByUser(UNIQUE_USERNAME)).thenReturn(Collections.emptyList());

        //when
        UserDto result = userServiceImpl.getUserInfo(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(quizService, times(1)).getAllUserQuizzes(anyString());
        verify(scoreService, times(1)).getScoresByUser(anyString());
        assertNotNull(result);
        assertEquals(ENABLED_USER.getEmail(), result.getEmail());
        assertEquals(0, result.getQuizAddedNumber());
        assertEquals(0, result.getQuizAttemptsNumber());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
        //given
        exception.expect(UsernameNotFoundException.class);
        exception.expectMessage("No user with that email or username exists!");
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(null);

        //when
        userServiceImpl.getUserInfo(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(quizService, never()).getAllUserQuizzes(anyString());
        verify(scoreService, never()).getScoresByUser(anyString());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenQuizServiceReturnNull() {
        //given
        exception.expect(NullPointerException.class);
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(quizService.getAllUserQuizzes(UNIQUE_USERNAME)).thenReturn(null);

        //when
        userServiceImpl.getUserInfo(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(quizService, times(1)).getAllUserQuizzes(anyString());
        verify(scoreService, never()).getScoresByUser(anyString());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenScoreServiceReturnNull() {
        //given
        exception.expect(NullPointerException.class);
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(quizService.getAllUserQuizzes(UNIQUE_USERNAME)).thenReturn(Collections.emptyList());
        when(scoreService.getScoresByUser(UNIQUE_USERNAME)).thenReturn(null);

        //when
        userServiceImpl.getUserInfo(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(quizService, times(1)).getAllUserQuizzes(anyString());
        verify(scoreService, times(1)).getScoresByUser(anyString());
    }

    @Test
    public void shouldUpdateUserPassword() {
        //given
        String oldPassword = "pass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).thenReturn(true);

        //when
        String result = userServiceImpl.updateUserPassword(UNIQUE_USERNAME, passwordDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Password successfully changed");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidOldPassword() {
        //given
        String oldPassword = "invalidPass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, newPassword);
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Wrong password!");
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).thenReturn(false);

        //when
        userServiceImpl.updateUserPassword(UNIQUE_USERNAME, passwordDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenInvalidNewPasswordConfirmation() {
        //given
        String oldPassword = "invalidPass";
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(oldPassword, newPassword, "invalidConfirmation");
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The Password confirmation must match New password!");
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(passwordEncoder.matches(passwordDto.getOldPassword(), ENABLED_USER.getPassword())).thenReturn(true);

        //when
        userServiceImpl.updateUserPassword(UNIQUE_USERNAME, passwordDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
    }

    @Test
    public void shouldResetUserPassword() {
        //given
        String newPassword = "newPass";
        PasswordDto passwordDto = new PasswordDto(null, newPassword, newPassword);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(ENABLED_USER));

        //when
        String result = userServiceImpl.resetUserPassword(USER_ID, passwordDto);

        //then
        verify(userRepository, times(1)).findById(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Password successfully changed");
    }

    @Test
    public void shouldDeleteUser() {
        //given
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);

        //when
        String result = userServiceImpl.deleteUser(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).delete(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(result, "Account successfully deleted");
    }
}