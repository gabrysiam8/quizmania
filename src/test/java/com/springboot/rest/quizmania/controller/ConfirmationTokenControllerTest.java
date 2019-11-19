package com.springboot.rest.quizmania.controller;

import com.springboot.rest.quizmania.domain.ConfirmationToken;
import com.springboot.rest.quizmania.service.ConfirmationTokenService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.CONFIRMATION_TOKEN;
import static com.springboot.rest.quizmania.common.TestData.DISABLED_USER;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ConfirmationTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConfirmationTokenService service;

    @Test
    public void shouldReturnConfirmationTokenWhenSuccessfullyConfirmed() throws Exception {
        ConfirmationToken confirmationToken = new ConfirmationToken(CONFIRMATION_TOKEN, DISABLED_USER);
        given(service.confirmToken(anyString())).willReturn(confirmationToken);

        mockMvc.perform(get("/confirmation")
            .param("token",CONFIRMATION_TOKEN))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.token").value(CONFIRMATION_TOKEN))
               .andExpect(jsonPath("$.user.username").value(DISABLED_USER.getUsername()));
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidToken() throws Exception {
        Exception expectedException = new IllegalArgumentException("Invalid token.");
        given(service.confirmToken(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/confirmation")
            .param("token","invalidToken"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }
}