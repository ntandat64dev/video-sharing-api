package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.repository.FollowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class UserControllerTest {

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private @Autowired FollowRepository followRepository;

    private final String userId = "3f06af63";
    private final String userId2 = "a05990b1";

    @Test
    public void givenUserId_whenGetUser_thenReturnSuccessful() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3f06af63"))
                .andExpect(jsonPath("$.snippet.username").value("user"))
                .andExpect(jsonPath("$.statistic.viewCount").value(7))
                .andExpect(jsonPath("$.statistic.followerCount").value(1))
                .andExpect(jsonPath("$.statistic.followingCount").value(0))
                .andExpect(jsonPath("$.statistic.videoCount").value(2));
    }

    @Test
    public void givenUserId_whenGetFollows_thenReturnSuccessful() throws Exception {
        mockMvc.perform(get("/api/v1/users/follows")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("f2cf8a48"));
    }

    @Test
    public void givenFollowerIdAndUserId_whenGetFollow_thenReturnSuccessful() throws Exception {
        mockMvc.perform(get("/api/v1/users/follows")
                        .param("userId", userId2)
                        .param("forUserId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("f2cf8a48"));
    }

    @Test
    @Transactional
    public void givenFollowDto_whenFollow_thenReturnSuccessful() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(userId2)
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(userId)
                .build());

        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.username").value("user1"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(1))
                .andExpect(jsonPath("$.followerSnippet.thumbnails.length()").value(2));
    }

    @Test
    @Transactional
    public void givenFollowDtoThatAlreadyExists_whenFollow_thenReturnError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId("3f06af63")
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId("a05990b1")
                .build());

        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("The follow that you are trying to create already exists."));
    }

    @Test
    @Transactional
    public void givenFollowDtoWithTheSameUserId_whenFollow_thenReturnError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(userId)
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(userId)
                .build());

        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Follow to yourself is not supported."));
    }

    @Test
    @Transactional
    public void givenFollowId_whenDelete_thenReturnSuccessful() throws Exception {
        mockMvc.perform(delete("/api/v1/users/follows")
                        .param("id", "f2cf8a48"))
                .andExpect(status().isNoContent());
        assertThat(followRepository.existsById("f2cf8a48"))
                .isFalse();
    }

    @Test
    @Transactional
    public void givenInvalidFollowId_whenDelete_thenReturnError() throws Exception {
        mockMvc.perform(delete("/api/v1/users/follows"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: must not be null"));

        mockMvc.perform(delete("/api/v1/users/follows")
                        .param("id", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: does not exist"));

        mockMvc.perform(delete("/api/v1/users/follows")
                        .param("id", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",
                        containsString("id: does not exist")));
    }

    @Test
    public void givenUserId_whenGetVideoCategories_thenReturnSuccessful() throws Exception {
        mockMvc.perform(get("/api/v1/users/video-categories")
                        .param("userId", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("music"));
    }
}
