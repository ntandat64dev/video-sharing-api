package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.dto.response.ErrorResponse;
import com.example.videosharingapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class AuthControllerTest {

    private @Autowired UserRepository userRepository;

    private @Autowired TestUtil testUtil;
    private @Autowired MockMvc mockMvc;

    @Test
    public void givenEmailAndPassword_whenLogin_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<UserDto>();
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, UserDto.class))
                .andExpect(status().isOk());
        assertThat(response.get().getId())
                .isEqualTo(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"));
    }

    @Test
    public void givenInvalidEmailAndPassword_whenLogin_thenReturnErrorResponse() throws Exception {
        var errorResponse = new AtomicReference<ErrorResponse>();

        // Given wrong email or password.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "11111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Email or password is incorrect.");

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Invalid email format.");

        // Given invalid password length.
        mockMvc.perform(post("/api/v1/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "0000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Password must be at least 8 characters.");
    }

    @Test
    @Transactional
    public void givenEmailAndPassword_whenSignup_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<UserDto>();
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@gmail.com")
                        .param("password", "11111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, UserDto.class))
                .andExpect(status().isCreated());
        assertThat(response.get()).isNotNull();
    }

    @Test
    @Transactional
    public void givenEmailAndPassword_whenSignup_thenUserIsCreated() throws Exception {
        var response = new AtomicReference<UserDto>();
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@gmail.com")
                        .param("password", "11111111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, UserDto.class))
                .andExpect(status().isCreated());
        var user = userRepository.findByEmail("user1@gmail.com");
        assertThat(user.getEmail()).isEqualTo("user1@gmail.com");
        assertThat(user.getPassword()).isEqualTo("11111111");
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        assertThat(user.getGender()).isNull();
        assertThat(user.getCountry()).isNull();
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getBio()).isNull();
        assertThat(user.getPublishedAt()).isEqualTo(response.get().getSnippet().getPublishedAt());
        assertThat(user.getThumbnails()).hasSize(2);
    }

    @Test
    @Transactional
    public void givenInvalidEmailAndPassword_whenSignup_thenReturnErrorResponse() throws Exception {
        var errorResponse = new AtomicReference<ErrorResponse>();

        // Given email that already exists.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user@gmail.com")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Email is already exists.");

        // Given invalid email format.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1@")
                        .param("password", "00000000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Invalid email format.");

        // Given invalid email format and invalid password length.
        mockMvc.perform(post("/api/v1/auth/signup")
                        .param("email", "user1")
                        .param("password", "0000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());
        assertThat(errorResponse.get().getErrorMessage())
                .contains("Invalid email format.")
                .contains("Password must be at least 8 characters.");
    }
}
