package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.VideoDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class VideoControllerTest {

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private final UUID userId = UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f");
    private final UUID videoId = UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978");
    private final MockMultipartFile mockVideoFile = new MockMultipartFile(
            "videoFile",
            "test.mp4",
            "video/mp4", RandomStringUtils.random(10).getBytes());
    private VideoDto videoDto;

    @BeforeEach
    public void setUpVideoDto() {
        // Set up VideoDto.
        videoDto = new VideoDto();
        videoDto.setSnippet(VideoDto.Snippet.builder()
                .title("Video test")
                .description("Video description test")
                .build());
        videoDto.setStatus(VideoDto.Status.builder()
                .privacy("private")
                .madeForKids(false)
                .ageRestricted(false)
                .commentAllowed(true)
                .build());
    }

    @Test
    public void givenMultipartFiles_whenPostVideo_thenResponseSuccessful() throws Exception {
        videoDto.getSnippet().setUserId(userId);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Video test")));
    }

    @Test
    public void givenMultipartFilesWithInvalidUserId_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var randomUserId = UUID.randomUUID();
        videoDto.getSnippet().setUserId(randomUserId);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string(containsString("User ID %s is not exist.".formatted(randomUserId.toString()))));
    }

    @Test
    public void givenMultipartFilesButMissingVideoFile_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required part 'videoFile' is not present.")));
    }

    @Test
    public void givenMultipartFilesButMissingUserId_whenPostVideo_thenReturnErrorResponse() throws Exception {
        videoDto.getSnippet().setUserId(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID is required")));
    }

    @Test
    public void givenMultipartFilesButMissingTitle_whenPostVideo_thenReturnErrorResponse() throws Exception {
        videoDto.getSnippet().setTitle(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Video title is required")));
    }

    @Test
    public void givenMultipartFilesButMissingPrivacy_whenPostVideo_thenReturnErrorResponse() throws Exception {
        videoDto.getStatus().setPrivacy(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Privacy is required")));
    }

    @Test
    public void givenInvalidPrivacyStatus_whenPostVideo_thenReturnErrorResponse() throws Exception {
        videoDto.getStatus().setPrivacy("privates");
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Privacy must either 'private' or 'public'.")));
    }

    @Test
    public void givenUserId_whenGetVideosByAllCategories_thenReturnSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/api/v1/videos/category/all")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Video 1")))
                .andExpect(content().string(containsString("Video 2")));
    }

    @Test
    public void giveUserIdAndVideoId_whenGetRating_thenReturnSuccessful() throws Exception {
        mockMvc.perform(get("/api/v1/videos/rate")
                        .param("userId", userId.toString())
                        .param("videoId", videoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("like")));
    }

    @Test
    public void givenUserIdAndVideoId_whenGetRelatedVideo_thenReturnSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/api/v1/videos/related")
                        .param("userId", userId.toString())
                        .param("videoId", videoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Video 1")))
                .andExpect(content().string(containsString("Video 2")));
    }
}