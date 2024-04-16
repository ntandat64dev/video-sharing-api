package com.example.videosharingapi.controller;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.UserRepository;
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
    private @Autowired UserRepository userRepository;
    private @Autowired ObjectMapper objectMapper;

    private User user;
    private VideoDto videoDto;
    private MockMultipartFile mockVideoFile;

    @BeforeEach
    public void setUp() {
        // Set up User.
        user = userRepository.findById(UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f")).orElseThrow();

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

        // Set up mock video file.
        mockVideoFile = new MockMultipartFile("videoFile", "test.mp4",
                "video/mp4", RandomStringUtils.random(10).getBytes());
    }

    @Test
    public void givenGetRecommendVideosUri_whenGet_thenReturnSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/api/v1/videos/recommend")
                        .param("userId", user.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Video 1")))
                .andExpect(content().string(containsString("Video 2")));
    }

    @Test
    public void givenMultipartFiles_whenPostVideo_thenResponseSuccessful() throws Exception {
        videoDto.getSnippet().setUserId(user.getId());
        var metadata = new MockMultipartFile("metadata", null,
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
        var metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .string(containsString("User ID %s is not exist.".formatted(randomUserId.toString()))));
    }

    @Test
    public void givenMissingInfo_whenPostVideo_thenReturnErrorResponse() throws Exception {
        // Given missing video file.
        var metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required part 'videoFile' is not present.")));

        // Given missing title metadata.
        videoDto.getSnippet().setTitle(null);
        metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Video title is required")));

        // Given missing privacy metadata.
        videoDto.getSnippet().setTitle("Video test");
        videoDto.getStatus().setPrivacy(null);
        metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Privacy is required")));

        // Given missing user ID.
        videoDto.getStatus().setPrivacy("private");
        videoDto.getSnippet().setUserId(null);
        metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID is required")));
    }

    @Test
    public void givenInvalidPrivacyStatus_whenPostVideo_thenReturnErrorResponse() throws Exception {
        videoDto.getStatus().setPrivacy("privates");
        var metadata = new MockMultipartFile("metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Privacy must either 'private' or 'public'.")));
    }
}