package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.AbstractElasticsearchContainer;
import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class UserControllerTest extends AbstractElasticsearchContainer {

    private @Autowired MockMvc mockMvc;
    private @Autowired UserRepository userRepository;

    @Test
    @Transactional
    public void givenImage_whenChangeProfileImage_thenSuccess() throws Exception {
        var mockImageFile = new MockMultipartFile(
                "imageFile",
                "thumbnail.png",
                "image/png", RandomStringUtils.random(2).getBytes());

        mockMvc.perform(multipart("/api/v1/users/profile-image")
                        .file(mockImageFile))
                .andExpect(status().isOk());

        var user = userRepository.findById("a05990b1").orElseThrow();
        assertThat(user.getThumbnails().size()).isEqualTo(1);
        assertThat(user.getThumbnails().get(0).getUrl())
                .isEqualTo("https://dummyimage.com/720x450/ff6b81/fff");
    }

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
