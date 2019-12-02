package com.springboot.rest.quizmania.controller;

import java.util.List;

import com.springboot.rest.quizmania.service.DifficultyLevelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DifficultyLevelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DifficultyLevelService service;

    @Test
    public void shouldReturnAllDifficultyLevels() throws Exception {
        List<String> levels = List.of("EASY", "NORMAL", "HARD", "EXPERT");
        given(service.getAllDifficultyLevels()).willReturn(levels);

        mockMvc.perform(get("/level"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(4))
               .andExpect(jsonPath("$[0]").value(levels.get(0)))
               .andExpect(jsonPath("$[1]").value(levels.get(1)))
               .andExpect(jsonPath("$[2]").value(levels.get(2)))
               .andExpect(jsonPath("$[3]").value(levels.get(3)));
    }

}