package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.FollowDto;
import com.example.videosharingapi.dto.UserDto;
import com.example.videosharingapi.dto.response.ErrorResponse;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class UserControllerTest {

    private @Autowired TestUtil testUtil;
    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private final String userId = "3f06af63-a93c-11e4-9797-00505690773f";
    private final String userId2 = "a05990b1-9110-40b1-aa4c-03951b0705de";

    @Test
    public void givenUserId_whenGetUser_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<UserDto>();
        mockMvc.perform(get("/api/v1/users")
                        .param("userId", userId))
                .andDo(result -> testUtil.toDto(result, response, UserDto.class))
                .andExpect(status().isOk());

        assertThat(response.get().getId())
                .isEqualTo(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"));
        assertThat(response.get().getSnippet().getUsername()).isEqualTo("user");
        assertThat(response.get().getStatistic().getViewCount()).isEqualTo(7);
        assertThat(response.get().getStatistic().getFollowerCount()).isEqualTo(1);
        assertThat(response.get().getStatistic().getFollowingCount()).isEqualTo(0);
        assertThat(response.get().getStatistic().getVideoCount()).isEqualTo(2);
    }

    @Test
    public void givenUserId_whenGetFollows_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<FollowDto[]>();
        mockMvc.perform(get("/api/v1/users/follows")
                        .param("userId", userId))
                .andDo(result -> testUtil.toDto(result, response, FollowDto[].class))
                .andExpect(status().isOk());
        assertThat(response.get()).hasSize(1);
        assertThat(response.get()[0].getId())
                .isEqualTo(UUID.fromString("f2cf8a48-02d6-4e04-a816-045521ee7b83"));
    }

    @Test
    public void givenFollowerIdAndUserId_whenGetFollow_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<FollowDto[]>();
        mockMvc.perform(get("/api/v1/users/follows")
                        .param("userId", userId2)
                        .param("forUserId", userId))
                .andDo(result -> testUtil.toDto(result, response, FollowDto[].class))
                .andExpect(status().isOk());

        assertThat(response.get()).hasSize(1);
        assertThat(response.get()[0].getId())
                .isEqualTo(UUID.fromString("f2cf8a48-02d6-4e04-a816-045521ee7b83"));
    }

    @Test
    @Transactional
    public void givenFollowDto_whenFollow_thenReturnSuccessful() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(UUID.fromString(userId2))
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(UUID.fromString(userId))
                .build());

        var response = new AtomicReference<FollowDto>();
        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, response, FollowDto.class))
                .andExpect(status().isCreated());

        assertThat(response.get().getSnippet().getUsername()).isEqualTo("user1");
        assertThat(response.get().getSnippet().getThumbnails()).hasSize(1);
        assertThat(response.get().getFollowerSnippet().getThumbnails()).hasSize(2);
    }

    @Test
    @Transactional
    public void givenFollowDtoThatAlreadyExists_whenFollow_thenReturnError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f"))
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(UUID.fromString("a05990b1-9110-40b1-aa4c-03951b0705de"))
                .build());

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("The follow that you are trying to create already exists.");
    }

    @Test
    @Transactional
    public void givenFollowDtoWithTheSameUserId_whenFollow_thenReturnError() throws Exception {
        var followDto = new FollowDto();
        followDto.setSnippet(FollowDto.Snippet.builder()
                .userId(UUID.fromString(userId))
                .build());
        followDto.setFollowerSnippet(FollowDto.FollowerSnippet.builder()
                .userId(UUID.fromString(userId))
                .build());

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(post("/api/v1/users/follows")
                        .content(objectMapper.writeValueAsBytes(followDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Subscribing to your own channel is not supported.");
    }

    @Test
    public void givenUserId_whenGetVideoCategories_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<String[]>();
        mockMvc.perform(get("/api/v1/users/video-categories")
                        .param("userId", userId))
                .andDo(result -> testUtil.toDto(result, response, String[].class))
                .andExpect(status().isOk());
        assertThat(response.get()).containsExactly("music");
    }
}
