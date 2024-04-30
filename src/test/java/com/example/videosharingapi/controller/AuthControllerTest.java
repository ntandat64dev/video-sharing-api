package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class AuthControllerTest {

    private @Autowired UserRepository userRepository;
    private @Autowired MockMvc mockMvc;

    @Test
    public void givenEmailAndPassword_whenLogin_thenReturnSuccessful() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value("3f06af63"));
    }

    @Test
    public void givenInvalidEmailAndPassword_whenLogin_thenReturnErrorResponse() throws Exception {
        // Given wrong email or password.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "11111111"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email or password is incorrect."))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@")
                        .param("password", "00000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("email: must be a well-formed email address"));

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "0000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("password: size must be between 8 and 2147483647"));
    }

    @Test
    @Transactional
    public void givenEmailAndPassword_whenSignup_thenReturnSuccessful() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@gmail.com")
                        .param("password", "11111111"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @Transactional
    public void givenEmailAndPassword_whenSignup_thenUserIsCreated() throws Exception {
        var result = mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@gmail.com")
                        .param("password", "11111111"))
                .andExpect(status().isCreated())
                .andReturn();

        var user = userRepository.findByEmail("user1@gmail.com");
        assertThat(user.getEmail()).isEqualTo("user1@gmail.com");
        assertThat(user.getPassword()).isEqualTo("11111111");
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        assertThat(user.getGender()).isNull();
        assertThat(user.getCountry()).isNull();
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getBio()).isNull();
        assertThat(user.getPublishedAt())
                .isEqualTo(JsonPath.read(result.getResponse().getContentAsString(), "$.snippet.publishedAt"));
        assertThat(user.getThumbnails()).hasSize(2);
    }

    @Test
    @Transactional
    public void givenInvalidEmailAndPassword_whenSignup_thenReturnErrorResponse() throws Exception {
        // Given email that already exists.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Email is already exists."));

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@")
                        .param("password", "00000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("email: must be a well-formed email address"));

        // Given invalid email format and invalid password length.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1")
                        .param("password", "0000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors",
                        containsInAnyOrder(
                                "email: must be a well-formed email address",
                                "password: size must be between 8 and 2147483647")));
    }
}
