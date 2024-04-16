package com.example.videosharingapi.service;

import com.example.videosharingapi.exception.ApplicationException;
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
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class AuthServiceTest {

    private @Autowired AuthService authService;
    private @Autowired UserRepository userRepository;
    private @Autowired ChannelRepository channelRepository;

    @AfterEach
    public void deleteSignedUpUser(TestInfo testInfo) {
        if (testInfo.getTags().contains("deleteSignedUpUser")) {
            userRepository.deleteByEmail("user1@gmail.com");
        }
    }

    @Test
    public void givenEmailAndPassword_whenSignIn_thenReturnSuccessfulResponse() {
        var response = authService.signIn("user@gmail.com", "00000000");
        assertThat(response.getSnippet().getEmail()).isEqualTo("user@gmail.com");
    }

    @Test
    public void givenInvalidEmailAndPassword_whenSignIn_thenReturnErrorResponse() {
        // Given invalid email format.
        assertThrows(ApplicationException.class, () -> authService.signIn("user@", "00000000"));

        // Given an empty email.
        assertThrows(ApplicationException.class, () -> authService.signIn("", "00000000"));

        // Given invalid password length.
        assertThrows(ApplicationException.class, () -> authService.signIn("user@gmail.com", "0000"));

        // Given an empty password.
        assertThrows(ApplicationException.class, () -> authService.signIn("user@gmail.com", ""));
    }

    @Test
    @Tag("deleteSignedUpUser")
    public void givenEmailAndPassword_whenSignUp_thenReturnSuccessfulResponse() {
        var response = authService.signUp("user1@gmail.com", "00000000");
        assertThat(response.getId()).isNotNull();
        assertThat(response.getSnippet().getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    public void givenEmailWhichAlreadyExists_whenSignUp_thenReturnErrorResponse() {
        assertThrows(ApplicationException.class, () -> authService.signUp("user@gmail.com", "00000000"));
    }

    @Test
    @Tag("deleteSignedUpUser")
    public void givenEmailAndPassword_whenSignUp_thenChannelIsCreated() {
        var authResponse = authService.signUp("user1@gmail.com", "00000000");
        assertThat(channelRepository.findByUserId(authResponse.getId())).isNotNull();
    }
}
