package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class UserServiceTest {

    private @Autowired UserService userService;

    private FollowDto followDto;

    private final UUID userId = UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f");
    private final UUID userId2 = UUID.fromString("a05990b1-9110-40b1-aa4c-03951b0705de");

    @BeforeEach
    public void setUp() {
        followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(userId2)
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(userId)
                .build());
    }

    // TODO: Test get user by ID

    @Test
    public void givenUserId_whenGetFollows_thenReturnSuccessful() {
        var response = userService.getFollowsByUserId(userId);
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getId()).isEqualTo(UUID.fromString("f2cf8a48-02d6-4e04-a816-045521ee7b83"));
    }

    @Test
    public void givenFollowerIdAndUserId_whenGetFollow_thenReturnSuccessful() {
        var follow = userService.getFollowsByFollowerIdAndUserId(userId2, userId);
        assertThat(follow.getId()).isEqualTo(UUID.fromString("f2cf8a48-02d6-4e04-a816-045521ee7b83"));
    }

    @Test
    @Transactional
    public void givenFollowDto_whenFollow_thenReturnSuccessful() {
        var response = userService.follow(followDto);
        assertThat(response).isNotNull();
        assertThat(response.getSnippet().getUsername()).isEqualTo("user1");
        assertThat(response.getSnippet().getThumbnails()).hasSize(1);
        assertThat(response.getFollowerSnippet().getThumbnails()).hasSize(2);
    }

    @Test
    public void givenFollowDtoThatAlreadyExists_whenFollow_thenThrowApplicationException() {
        followDto.getSnippet().setUserId(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"));
        followDto.getFollowerSnippet().setUserId(UUID.fromString("a05990b1-9110-40b1-aa4c-03951b0705de"));
        assertThrows(ApplicationException.class,
                () -> userService.follow(followDto),
                "exception.follow.already-exist");
    }

    @Test
    public void givenFollowDtoWithTheSameUserId_whenFollow_thenReturnError() {
        followDto.getFollowerSnippet().setUserId(UUID.fromString("a05990b1-9110-40b1-aa4c-03951b0705de"));
        assertThrows(ApplicationException.class,
                () -> userService.follow(followDto),
                "exception.follow.self-follow");
    }

    @Test
    public void givenUserId_whenGetBrowseKeywords_thenReturnSuccessful() {
        var response = userService.getBrowseKeywords(userId);
        assertThat(response).containsExactlyInAnyOrder("music");
    }
}
