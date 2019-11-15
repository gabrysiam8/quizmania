package com.springboot.rest.quizmania.service;

import java.util.Calendar;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.repository.ConfirmationTokenRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.springboot.rest.quizmania.common.TestData.CONFIRMATION_TOKEN;
import static com.springboot.rest.quizmania.common.TestData.DISABLED_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmationTokenServiceTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ConfirmationTokenRepository tokenRepository;

    private ConfirmationTokenService tokenService;

    private ConfirmationToken confirmationToken;

    @Before
    public void setUp() {
        tokenService = new ConfirmationTokenService(tokenRepository);

        confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
    }

    @Test
    public void shouldCreateToken() {
        //given
        when(tokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.createToken(DISABLED_USER);

        //then
        verify(tokenRepository, times(1)).save(any(ConfirmationToken.class));
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldReturnToken() {
        //given
        when(tokenRepository.findByToken(CONFIRMATION_TOKEN)).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.getConfirmationToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldConfirmToken() {
        //given
        when(tokenRepository.findByToken(CONFIRMATION_TOKEN)).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.confirmToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertNotNull(result);
        assertEquals(CONFIRMATION_TOKEN, result.getToken());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenNotExist() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid token.");
        when(tokenRepository.findByToken(CONFIRMATION_TOKEN)).thenReturn(null);

        //when
        ConfirmationToken result = tokenService.confirmToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenTokenExpired() {
        //given
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Token have expired.");
        ConfirmationToken confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
        confirmationToken.setExpirationDate(Calendar.getInstance().getTime());
        when(tokenRepository.findByToken(CONFIRMATION_TOKEN)).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.confirmToken(CONFIRMATION_TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
    }
}