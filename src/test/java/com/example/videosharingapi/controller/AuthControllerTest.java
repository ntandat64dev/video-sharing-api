package com.example.videosharingapi.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class AuthControllerTest {

    private @Autowired MockMvc mockMvc;

    @Test
    public void givenEmailAndPassword_whenLogin_thenReturnSuccessful() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("3f06af63-a93c-11e4-9797-00505690773f")));
    }

    @Test
    public void givenInvalidEmailAndPassword_whenLogin_thenReturnErrorResponse() throws Exception {
        // Given wrong email or password.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "11111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email or password is incorrect.")));

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "0000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }

    @Test
    public void givenEmailAndPassword_whenSignUp_thenReturnSuccessful() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@gmail.com")
                        .param("password", "11111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenInvalidEmailAndPassword_whenSignUp_thenReturnErrorResponse() throws Exception {
        // Given email that already exists.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already exists.")));

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1")
                        .param("password", "0000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }
}
