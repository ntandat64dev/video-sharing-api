package com.example.videosharingapi.service;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    public void givenEmailAndPassword_whenSignIn_thenReturnSuccessful() {
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
    @Transactional
    public void givenEmailAndPassword_whenSignUp_thenReturnSuccessful() {
        var response = authService.signUp("user1@gmail.com", "00000000");
        assertThat(response.getId()).isNotNull();
        assertThat(response.getSnippet().getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    @Transactional
    public void givenEmailAndPassword_whenSignUp_thenAssertUserIsCreated() {
        var response = authService.signUp("user1@gmail.com", "00000000");
        var user = userRepository.findById(response.getId()).orElseThrow();
        assertThat(user.getEmail()).isEqualTo("user1@gmail.com");
        assertThat(user.getPassword()).isEqualTo("00000000");
        assertThat(user.getDateOfBirth()).isNull();
        assertThat(user.getPhoneNumber()).isNull();
        assertThat(user.getGender()).isNull();
        assertThat(user.getCountry()).isNull();
        assertThat(user.getUsername()).isEqualTo("user1");
        assertThat(user.getBio()).isNull();
        assertThat(user.getPublishedAt()).isEqualTo(response.getSnippet().getPublishedAt());
        assertThat(user.getThumbnails()).hasSize(2);
    }

    @Test
    public void givenEmailWhichAlreadyExists_whenSignUp_thenReturnErrorResponse() {
        assertThrows(ApplicationException.class,
                () -> authService.signUp("user@gmail.com", "00000000"),
                "exception.email.exist");
    }
}
