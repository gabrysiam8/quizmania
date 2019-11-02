package com.springboot.rest.quizmania.service;

import java.util.Calendar;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import com.springboot.rest.quizmania.config.JwtTokenProvider;
import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.EmailDto;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private EmailSenderService emailSenderService;

    private AuthService authService;

    private CustomUser user;

    private CustomUser savedUser;

    private static final String UNIQUE_USERNAME = "test";

    private static final String UNIQUE_ID = "testId-1234";

    @Before
    public void setUp() {
        authService = new AuthService(userRepository, authenticationManager, passwordEncoder, tokenProvider, confirmationTokenService, emailSenderService);

        user = CustomUser.builder()
                         .email("test@gmail.com")
                         .username(UNIQUE_USERNAME)
                         .password("pass")
                         .build();

        savedUser = CustomUser.builder()
                         .id(UNIQUE_ID)
                         .email("test@gmail.com")
                         .username(UNIQUE_USERNAME)
                         .password("pass")
                         .build();
    }

    @Test
    public void shouldFindUserByUsername() {
        //given
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(savedUser);

        //when
        CustomUser result = authService.findUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        assertNotNull(result);
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
        //given
        exception.expect(UsernameNotFoundException.class);
        exception.expectMessage("No user with that email or username exists!");
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(null);

        //when
        authService.findUserByUsername(UNIQUE_USERNAME);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
    }

    @Test
    public void shouldRegisterUser() throws MessagingException {
        //given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(CustomUser.class))).thenReturn(savedUser);
        when(confirmationTokenService.createToken(savedUser)).thenReturn(new ConfirmationToken("token", savedUser));
        when(emailSenderService.createMimeMessage(any(EmailDto.class))).thenReturn(new MimeMessage((Session) null));

        //when
        CustomUser result = authService.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, times(1)).sendEmail(any(MimeMessage.class));
        assertNotNull(result);
        assertEquals(UNIQUE_ID, result.getId());
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmailAlreadyTaken() throws MessagingException {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("User with this email already exists!");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //when
        authService.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        verify(confirmationTokenService, never()).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUsernameAlreadyTaken() throws MessagingException {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("User with this username already exists!");
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        //when
        authService.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, never()).save(any(CustomUser.class));
        verify(confirmationTokenService, never()).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenTokenServiceReturnNull() throws MessagingException {
        //given
        exception.expect(NullPointerException.class);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(CustomUser.class))).thenReturn(savedUser);
        when(confirmationTokenService.createToken(savedUser)).thenReturn(null);

        //when
        authService.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, never()).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
    }

    @Test
    public void shouldThrowMessagingExceptionWhenEmailServiceThrowException() throws MessagingException {
        //given
        exception.expect(MessagingException.class);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(CustomUser.class))).thenReturn(savedUser);
        when(confirmationTokenService.createToken(savedUser)).thenReturn(new ConfirmationToken("token", savedUser));
        when(emailSenderService.createMimeMessage(any(EmailDto.class))).thenThrow(MessagingException.class);

        //when
        authService.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, never()).sendEmail(any(MimeMessage.class));
        verify(userRepository, times(1)).delete(any(CustomUser.class));
    }

    @Test
    public void shouldLoginUser(){
        //given
        savedUser.setEnabled(true);
        UserLoginDto loginDto = createLoginDto();
        String token = "secrettoken";

        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(savedUser);
        when(tokenProvider.generateToken(savedUser)).thenReturn(token);

        //when
        Map<String,String> result = authService.loginUser(loginDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tokenProvider, times(1)).generateToken(any(CustomUser.class));
        assertNotNull(result);
        assertEquals(token, result.get("token"));
    }

    @Test
    public void shouldThrowDisabledExceptionWhenUserDisabled(){
        //given
        exception.expect(DisabledException.class);
        exception.expectMessage("User account is locked!");
        UserLoginDto loginDto = createLoginDto();
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(savedUser);

        //when
        authService.loginUser(loginDto);

        //then
        verify(userRepository, times(1)).findByUsername(anyString());
        verify(tokenProvider, never()).generateToken(any(CustomUser.class));
    }

    private UserLoginDto createLoginDto() {
        UserLoginDto loginDto = new UserLoginDto();
        loginDto.setUsername(UNIQUE_USERNAME);
        loginDto.setPassword("pass");
        return loginDto;
    }

    @Test
    public void shouldConfirmUserAccount() {
        //given
        String token = "secrettoken";
        ConfirmationToken confirmationToken = new ConfirmationToken(token, savedUser);

        when(confirmationTokenService.getConfirmationToken(anyString())).thenReturn(confirmationToken);

        //when
        String result = authService.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).getConfirmationToken(anyString());
        assertNotNull(result);
        assertTrue(savedUser.isEnabled());
        assertEquals(result, "Account successfully verified.");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenNotExist() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid token.");
        String token = "secrettoken";
        when(confirmationTokenService.getConfirmationToken(anyString())).thenReturn(null);

        //when
        authService.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).getConfirmationToken(anyString());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenExpired() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Token have expired.");

        String token = "secrettoken";
        ConfirmationToken confirmationToken = new ConfirmationToken(token, savedUser);
        confirmationToken.setExpirationDate(Calendar.getInstance().getTime());

        when(confirmationTokenService.getConfirmationToken(anyString())).thenReturn(confirmationToken);

        //when
        authService.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).getConfirmationToken(anyString());
    }
}