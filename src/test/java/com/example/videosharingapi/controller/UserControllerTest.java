package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.contains;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class UserControllerTest {

    private @Autowired MockMvc mockMvc;

    @Test
    public void whenGetMyUserInfo_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("a05990b1"))
                .andExpect(jsonPath("$.snippet.username").value("user1"))
                .andExpect(jsonPath("$.snippet.roles").value(contains("USER")))
                .andExpect(jsonPath("$.statistic.viewCount").value(7))
                .andExpect(jsonPath("$.statistic.followerCount").value(0))
                .andExpect(jsonPath("$.statistic.followingCount").value(1))
                .andExpect(jsonPath("$.statistic.videoCount").value(2));
    }

    @Test
    public void givenUserId_whenGetUserById_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/users/{userId}", "9b79f4ba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("9b79f4ba"))
                .andExpect(jsonPath("$.snippet.username").value("user2"))
                .andExpect(jsonPath("$.snippet.roles").value(contains("USER")))
                .andExpect(jsonPath("$.statistic.viewCount").value(2))
                .andExpect(jsonPath("$.statistic.followerCount").value(2))
                .andExpect(jsonPath("$.statistic.followingCount").value(0))
                .andExpect(jsonPath("$.statistic.videoCount").value(1));
    }
}
