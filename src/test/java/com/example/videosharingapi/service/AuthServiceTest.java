package com.example.videosharingapi.service;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.payload.request.AuthRequest;
import com.example.videosharingapi.repository.ChannelRepository;
import com.example.videosharingapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS, config = @SqlConfig(commentPrefix = "#"))
public class AuthServiceTest {

    private @Autowired AuthService authService;
    private @Autowired UserRepository userRepository;
    private @Autowired ChannelRepository channelRepository;

    @AfterEach
    public void deleteSignedUpUserAndItsChannel(TestInfo testInfo) {
        if (testInfo.getTags().contains("deleteSignedUpUserAndItsChannel")) {
            channelRepository.deleteByUserEmail("user1@gmail.com");
            userRepository.deleteByEmail("user1@gmail.com");
        }
    }

    @Test
    public void givenAuthRequest_whenSignIn_thenReturnSuccessfulResponse() {
        var authRequest = new AuthRequest("user@gmail.com", "00000000");
        var authResponse = authService.signIn(authRequest);
        assertThat(authResponse.message()).isEqualTo("Sign in successfully.");
        assertThat(authResponse.userInfo().getEmail()).isEqualTo("user@gmail.com");
    }

    @Test
    public void givenAuthRequestWithWrongUserInfo_whenSignIn_thenReturnErrorResponse() {
        // Given invalid email format.
        var invalidEmailRequest = new AuthRequest("user@", "00000000");
        assertThrows(ApplicationException.class, () -> authService.signIn(invalidEmailRequest));

        // Given an empty email.
        var invalidEmailRequest2 = new AuthRequest("", "00000000");
        assertThrows(ApplicationException.class, () -> authService.signIn(invalidEmailRequest2));

        // Given invalid password length.
        var invalidPasswordRequest = new AuthRequest("user@gmail.com", "0000");
        assertThrows(ApplicationException.class, () -> authService.signIn(invalidPasswordRequest));

        // Given an empty password.
        var invalidPasswordRequest2 = new AuthRequest("user@gmail.com", "");
        assertThrows(ApplicationException.class, () -> authService.signIn(invalidPasswordRequest2));
    }

    @Test
    @Tag("deleteSignedUpUserAndItsChannel")
    public void givenAuthRequest_whenSignUp_thenReturnSuccessfulResponse() {
        var authRequest = new AuthRequest("user1@gmail.com", "00000000");
        var authResponse = authService.signUp(authRequest);
        assertThat(authResponse.userInfo().getId()).isNotNull();
        assertThat(authResponse.message()).isEqualTo("Sign up successfully.");
        assertThat(authResponse.userInfo().getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    public void givenAuthRequestWithEmailWhichAlreadyExists_whenSignUp_thenReturnErrorResponse() {
        var authRequest = new AuthRequest("user@gmail.com", "00000000");
        assertThrows(ApplicationException.class, () -> authService.signUp(authRequest));
    }

    @Test
    @Tag("deleteSignedUpUserAndItsChannel")
    public void givenAuthRequest_whenSignUp_thenChannelIsCreated() {
        var authRequest = new AuthRequest("user1@gmail.com", "00000000");
        var authResponse = authService.signUp(authRequest);
        assertThat(channelRepository.findByUserId(authResponse.userInfo().getId())).isNotNull();
    }
}
