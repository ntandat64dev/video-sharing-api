package com.example.videosharingapi.controller;

import com.example.videosharingapi.testutil.InsertDataExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ "dev", "test" })
@AutoConfigureMockMvc
@ExtendWith(InsertDataExtension.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenLoginURI_whenMockMVC_thenVerifyResponse() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .content("{\"email\": \"user@gmail.com\", \"password\": \"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign in successfully.")));
    }

    @Test
    public void givenLoginURIWithIncorrectInfo_whenMockMVC_thenVerifyResponse() throws Exception {
        // Given wrong email or password.
        mockMvc.perform(post("/api/auth/login")
                        .content("{\"email\": \"user@gmail.com\", \"password\": \"11111111\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email or password is incorrect.")));

        // Given invalid email format.
        mockMvc.perform(post("/api/auth/login")
                        .content("{\"email\": \"user@\", \"password\": \"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));

        // Given invalid password length.
        mockMvc.perform(post("/api/auth/login")
                        .content("{\"email\": \"user@gmail.com\", \"password\": \"0000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }

    @Test
    public void givenSignUpURI_whenMockMVC_thenVerifyResponse() throws Exception {
        mockMvc.perform(post("/api/auth/signup")
                        .content("{\"email\": \"user1@gmail.com\", \"password\": \"11111111\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Sign up successfully.")));
    }

    @Test
    public void givenSignUpURIWithIncorrectInfo_whenMockMVC_thenVerifyResponse() throws Exception {
        // Given email that already exists.
        mockMvc.perform(post("/api/auth/signup")
                        .content("{\"email\": \"user@gmail.com\", \"password\": \"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already exists.")));

        // Given invalid email format.
        mockMvc.perform(post("/api/auth/signup")
                        .content("{\"email\": \"user1@\", \"password\": \"00000000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid email format")));

        // Given invalid password length.
        mockMvc.perform(post("/api/auth/signup")
                        .content("{\"email\": \"user1@gmail.com\", \"password\": \"0000\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be at least 8 characters")));
    }
}
