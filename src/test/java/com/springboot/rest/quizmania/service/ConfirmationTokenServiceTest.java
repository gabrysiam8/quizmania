package com.springboot.rest.quizmania.service;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.repository.ConfirmationTokenRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository tokenRepository;

    private ConfirmationTokenService tokenService;

    private ConfirmationToken confirmationToken;

    private static final CustomUser USER = CustomUser.builder()
                                .email("test@gmail.com")
                                .username("test")
                                .password("pass")
                                .build();

    private static final String TOKEN = "token-1234";

    @Before
    public void setUp() {
        tokenService = new ConfirmationTokenService(tokenRepository);

        confirmationToken = new ConfirmationToken(TOKEN, USER);
    }

    @Test
    public void shouldCreateToken() {
        //given
        when(tokenRepository.save(any(ConfirmationToken.class))).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.createToken(USER);

        //then
        verify(tokenRepository, times(1)).save(any(ConfirmationToken.class));
        assertNotNull(result);
        assertEquals(TOKEN, result.getToken());
    }

    @Test
    public void shouldReturnToken() {
        //given

        when(tokenRepository.findByToken(TOKEN)).thenReturn(confirmationToken);

        //when
        ConfirmationToken result = tokenService.getConfirmationToken(TOKEN);

        //then
        verify(tokenRepository, times(1)).findByToken(anyString());
        assertNotNull(result);
        assertEquals(TOKEN, result.getToken());
    }
}