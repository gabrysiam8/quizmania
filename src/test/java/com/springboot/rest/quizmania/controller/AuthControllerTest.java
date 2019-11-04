package com.springboot.rest.quizmania.controller;

import java.util.Map;
import javax.mail.MessagingException;

import com.springboot.rest.quizmania.domain.CustomUser;
import com.springboot.rest.quizmania.dto.UserLoginDto;
import com.springboot.rest.quizmania.service.AuthService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.springboot.rest.quizmania.common.TestData.DISABLED_USER;
import static com.springboot.rest.quizmania.common.TestUtils.readFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService service;

    @Test
    public void shouldReturnSuccessMessageWhenAccountCreated() throws Exception {
        given(service.registerUser(any(CustomUser.class))).willReturn(DISABLED_USER);

        mockMvc.perform(post("/auth/register")
            .content(readFile("requests/user.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isCreated())
               .andExpect(content().string("Account successfully created"));
    }

    @Test
    public void shouldReturnConflictWhenEmailAlreadyExist() throws Exception {
        Exception expectedException = new IllegalArgumentException("User with this email already exists!");
        given(service.registerUser(any(CustomUser.class))).willThrow(expectedException);

        mockMvc.perform(post("/auth/register")
            .content(readFile("requests/user.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isConflict())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    public void shouldReturnBadGatewayWhenEmailCannotBeSent() throws Exception {
        Exception expectedException = new MessagingException("Email cannot be sent!");
        given(service.registerUser(any(CustomUser.class))).willThrow(expectedException);

        mockMvc.perform(post("/auth/register")
            .content(readFile("requests/user.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isBadGateway())
               .andExpect(content().string(expectedException.getMessage()));
    }

    @Test
    public void shouldReturnTokenWhenUserSuccessfullyLoggedIn() throws Exception {
        Map<String,String> response = Map.of("tokenType", "Bearer", "token", "testJsonWebToken");
        given(service.loginUser(any(UserLoginDto.class))).willReturn(response);

        mockMvc.perform(post("/auth/login")
            .content(readFile("requests/login.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.tokenType").value("Bearer"))
               .andExpect(jsonPath("$.token").value("testJsonWebToken"));

    }

    @Test
    public void shouldReturnUnauthorizedWhenBadCredentials() throws Exception {
        Exception expectedException = new UsernameNotFoundException("No user with that email or username exists!");
        given(service.loginUser(any(UserLoginDto.class))).willThrow(expectedException);

        mockMvc.perform(post("/auth/login")
            .content(readFile("requests/login.json"))
            .contentType(APPLICATION_JSON))
               .andExpect(status().isUnauthorized())
               .andExpect(content().string(expectedException.getMessage()));

    }

    @Test
    public void shouldReturnSuccessMessageWhenAccountVerified() throws Exception {
        String expectedMessage = "Account successfully verified.";
        given(service.confirmUserAccount(anyString())).willReturn(expectedMessage);

        mockMvc.perform(get("/auth/confirmation")
            .param("token", "validToken"))
               .andExpect(status().isOk())
               .andExpect(content().string(expectedMessage));
    }

    @Test
    public void shouldReturnBadRequestWhenInvalidToken() throws Exception {
        Exception expectedException = new IllegalArgumentException("Invalid token.");
        given(service.confirmUserAccount(anyString())).willThrow(expectedException);

        mockMvc.perform(get("/auth/confirmation")
            .param("token", "invalidToken"))
               .andExpect(status().isBadRequest())
               .andExpect(content().string(expectedException.getMessage()));
    }
}