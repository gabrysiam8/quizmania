package com.springboot.rest.quizmania.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.springboot.rest.quizmania.common.TestData.DISABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.ENABLED_USER;
import static com.springboot.rest.quizmania.common.TestData.UNIQUE_USERNAME;
import static com.springboot.rest.quizmania.common.TestData.USER_ID;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceImplTest {

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

    private AuthServiceImpl authServiceImpl;

    private CustomUser user;

    @Before
    public void setUp() {
        authServiceImpl = new AuthServiceImpl(userRepository, authenticationManager, passwordEncoder, tokenProvider, confirmationTokenService, emailSenderService);

        user = CustomUser.builder()
                         .email("test@gmail.com")
                         .username(UNIQUE_USERNAME)
                         .password("pass")
                         .build();
    }

//    @Test
//    public void shouldFindUserByUsername() {
//        //given
//        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(DISABLED_USER);
//
//        //when
//        CustomUser result = authServiceImpl.findUserByUsername(UNIQUE_USERNAME);
//
//        //then
//        verify(userRepository, times(1)).findByUsername(anyString());
//        assertNotNull(result);
//        assertEquals(UNIQUE_USERNAME, result.getUsername());
//    }
//
//    @Test
//    public void shouldThrowUsernameNotFoundExceptionWhenUserNotExist() {
//        //given
//        exception.expect(UsernameNotFoundException.class);
//        exception.expectMessage("No user with that email or username exists!");
//        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(null);
//
//        //when
//        authServiceImpl.findUserByUsername(UNIQUE_USERNAME);
//
//        //then
//        verify(userRepository, times(1)).findByUsername(anyString());
//    }

    @Test
    public void shouldRegisterUser() throws MessagingException {
        //given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(CustomUser.class))).thenReturn(DISABLED_USER);
        when(confirmationTokenService.createToken(any(CustomUser.class))).thenReturn(new ConfirmationToken("token", DISABLED_USER));
        when(emailSenderService.createMimeMessage(any(EmailDto.class))).thenReturn(new MimeMessage((Session) null));

        //when
        CustomUser result = authServiceImpl.registerUser(user);

        //then
        verify(userRepository, times(1)).existsByEmail(anyString());
        verify(userRepository, times(1)).existsByUsername(anyString());
        verify(userRepository, times(1)).save(any(CustomUser.class));
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, times(1)).sendEmail(any(MimeMessage.class));
        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(UNIQUE_USERNAME, result.getUsername());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmailAlreadyTaken() throws MessagingException {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("User with this email already exists!");
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        //when
        authServiceImpl.registerUser(user);

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
        authServiceImpl.registerUser(user);

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
        when(userRepository.save(any(CustomUser.class))).thenReturn(DISABLED_USER);
        when(confirmationTokenService.createToken(any(CustomUser.class))).thenReturn(null);

        //when
        authServiceImpl.registerUser(user);

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
        when(userRepository.save(any(CustomUser.class))).thenReturn(DISABLED_USER);
        when(confirmationTokenService.createToken(any(CustomUser.class))).thenReturn(new ConfirmationToken("token", DISABLED_USER));
        when(emailSenderService.createMimeMessage(any(EmailDto.class))).thenThrow(MessagingException.class);

        //when
        authServiceImpl.registerUser(user);

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
        UserLoginDto loginDto = createLoginDto();
        String token = "secrettoken";

        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(ENABLED_USER);
        when(tokenProvider.generateToken(ENABLED_USER)).thenReturn(token);

        //when
        Map<String,String> result = authServiceImpl.loginUser(loginDto);

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
        when(userRepository.findByUsername(UNIQUE_USERNAME)).thenReturn(DISABLED_USER);

        //when
        authServiceImpl.loginUser(loginDto);

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
        ConfirmationToken confirmationToken = new ConfirmationToken(token, DISABLED_USER);

        when(confirmationTokenService.confirmToken(anyString())).thenReturn(confirmationToken);

        //when
        String result = authServiceImpl.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
        assertNotNull(result);
        assertEquals(result, "Account successfully verified.");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenNotExist() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid token.");
        String token = "secrettoken";
        Exception expectedException = new IllegalArgumentException("Invalid token.");
        when(confirmationTokenService.confirmToken(anyString())).thenThrow(expectedException);

        //when
        authServiceImpl.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenExpired() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Token have expired.");
        String token = "secrettoken";
        Exception expectedException = new IllegalArgumentException("Token have expired.");
        when(confirmationTokenService.confirmToken(anyString())).thenThrow(expectedException);

        //when
        authServiceImpl.confirmUserAccount(token);

        //then
        verify(confirmationTokenService, times(1)).confirmToken(anyString());
    }

    @Test
    public void shouldSendResetPasswordEmail() throws MessagingException {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(ENABLED_USER);
        when(confirmationTokenService.createToken(any(CustomUser.class))).thenReturn(new ConfirmationToken("token", ENABLED_USER));
        when(emailSenderService.createMimeMessage(any(EmailDto.class))).thenReturn(new MimeMessage((Session) null));

        //when
        String result = authServiceImpl.sendResetPasswordEmail(ENABLED_USER.getEmail());

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(confirmationTokenService, times(1)).createToken(any(CustomUser.class));
        verify(emailSenderService, times(1)).createMimeMessage(any(EmailDto.class));
        verify(emailSenderService, times(1)).sendEmail(any(MimeMessage.class));
        assertNotNull(result);
        assertEquals(result, "Email successfully send");
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenEmailNotExist() throws MessagingException {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("User with that email not exists!");
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        //when
        String result = authServiceImpl.sendResetPasswordEmail("invalid@gmail.com");

        //then
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}