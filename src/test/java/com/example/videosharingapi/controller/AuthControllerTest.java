package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.entity.Role;
import com.example.videosharingapi.repository.ThumbnailRepository;
import com.example.videosharingapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class AuthControllerTest {

    private @Autowired UserRepository userRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;
    private @Autowired MockMvc mockMvc;

    @Test
    public void givenUsernameAndPassword_whenLogin_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("username", "user1")
                        .param("password", "11111111"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    public void givenInvalidCredentials_whenLogin_thenError() throws Exception {
        // Given wrong username.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("username", "user")
                        .param("password", "11111111"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username or password is incorrect."))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Given wrong password.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("username", "user")
                        .param("password", "12345678"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username or password is incorrect."))
                .andExpect(jsonPath("$.errors").doesNotExist());

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("username", "user1")
                        .param("password", "1111"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("password: size must be between 8 and 2147483647"));
    }

    @Test
    @Transactional
    public void givenUsernameAndPassword_whenSignup_thenSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("username", "user3")
                        .param("password", "33333333"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @Transactional
    public void givenUsernameAndPassword_whenSignup_thenUserIsCreated() throws Exception {
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("username", "user3")
                        .param("password", "33333333"))
                .andExpect(status().isCreated());

        var user = userRepository.findByUsername("user3");
        assertThat(user.getUsername()).isEqualTo("user3");
        assertThat(user.getPassword()).matches("\\A\\$2a?\\$\\d\\d\\$[./0-9A-Za-z]{53}");
        assertThat(user.getEmail()).isNull();
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        assertThat(user.getGender()).isNull();
        assertThat(user.getCountry()).isNull();
        assertThat(user.getBio()).isNull();
        assertThat(user.getPublishedAt()).isNotNull();
        assertThat(user.getThumbnails()).hasSize(2);
        assertThat(user.getRoles().stream().map(Role::getName)).containsExactly("USER");

        // Assert Thumbnail is created.
        assertThat(thumbnailRepository.count()).isEqualTo(9);
    }

    @Test
    @Transactional
    public void givenInvalidCredentials_whenSignup_thenError() throws Exception {
        // Given username that already exists.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("username", "user1")
                        .param("password", "00000000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Username is already exists."));

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("username", "user3")
                        .param("password", "3333"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("password: size must be between 8 and 2147483647"));
    }
}
