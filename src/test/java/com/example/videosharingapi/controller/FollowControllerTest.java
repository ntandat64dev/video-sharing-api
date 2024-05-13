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
import org.springframework.security.test.context.support.WithUserDetails;
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
@WithUserDetails("user1")
public class FollowControllerTest {

    private @Autowired ObjectMapper objectMapper;
    private @Autowired FollowRepository followRepository;
    private @Autowired MockMvc mockMvc;

    // user2 follow user1
    private FollowDto obtainFollowDto() {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId("a05990b1")
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId("9b79f4ba")
                .build());
        return followDto;
    }

    @Test
    public void whenGetMyFollows_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/follows/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("f2cf8a48"));
    }

    @Test
    public void givenUserId_whenGetFollowOfUserIdThatFollowedByMe_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/follows/for-user")
                        .param("userId", "9b79f4ba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f2cf8a48"));
    }

    @Test
    @Transactional
    @WithUserDetails("user2")
    public void givenFollowDto_whenFollow_thenSuccess() throws Exception {
        var followDto = obtainFollowDto();

        mockMvc.perform(post("/api/v1/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.username").value("user1"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(2))
                .andExpect(jsonPath("$.followerSnippet.userId").value("9b79f4ba"))
                .andExpect(jsonPath("$.followerSnippet.thumbnails.length()").value(1));

        assertThat(followRepository.findByUserIdAndFollowerId("a05990b1", "9b79f4ba"))
                .isNotNull();
    }

    @Test
    @Transactional
    public void givenFollowDtoThatAlreadyExists_whenFollow_thenError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId("9b79f4ba")
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId("a05990b1")
                .build());

        mockMvc.perform(post("/api/v1/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("The follow that you are trying to create already exists."));
    }

    @Test
    @Transactional
    public void givenFollowDtoWithTheSameUserId_whenFollow_thenError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId("a05990b1")
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId("a05990b1")
                .build());

        mockMvc.perform(post("/api/v1/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Follow to yourself is not supported."));
    }

    @Test
    @Transactional
    public void givenFollowId_whenDelete_thenSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/follows")
                        .param("id", "f2cf8a48"))
                .andExpect(status().isNoContent());
        assertThat(followRepository.existsById("f2cf8a48")).isFalse();
    }

    @Test
    @WithUserDetails("user2")
    public void givenFollowId_whenDeleteWithInvalidUser_thenError() throws Exception {
        mockMvc.perform(delete("/api/v1/follows")
                        .param("id", "f2cf8a48"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenInvalidFollowId_whenDelete_thenError() throws Exception {
        mockMvc.perform(delete("/api/v1/follows"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: must not be null"));

        mockMvc.perform(delete("/api/v1/follows")
                        .param("id", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: ID does not exist."));

        mockMvc.perform(delete("/api/v1/follows")
                        .param("id", UUID.randomUUID().toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]",
                        containsString("id: ID does not exist.")));
    }
}
