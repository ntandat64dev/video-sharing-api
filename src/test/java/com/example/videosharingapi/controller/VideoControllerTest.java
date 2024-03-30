package com.example.videosharingapi.controller;

import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.testutil.InsertDataExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({ "dev", "test" })
@AutoConfigureMockMvc
@ExtendWith(InsertDataExtension.class)
public class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenGetAllVideoUri_whenGet_thenReturnSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Video 1")))
                .andExpect(content().string(containsString("Video 2")));
    }

    @Test
    public void givenMultipartFiles_whenPostVideo_thenResponseSuccessful() throws Exception {
        var user = userRepository.findByEmail("user@gmail.com");
        var videoFileTest = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", "data".getBytes());
        var metadata = new MockMultipartFile("metadata", null, "application/json", """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "visibility": "private",
                    "userId": "%s"
                }""".formatted(user.getId()).getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Video 3")));
    }

    @Test
    public void givenMultipartFilesWithInvalidUserId_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoFileTest = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", "data".getBytes());
        var randomUserId = UUID.randomUUID().toString();
        var metadata = new MockMultipartFile("metadata", null, "application/json", """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "visibility": "private",
                    "userId": "%s"
                }""".formatted(randomUserId).getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID %s is not exist.".formatted(randomUserId))));
    }

    @Test
    public void givenMissingInfo_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var user = userRepository.findByEmail("user@gmail.com");

        // Given missing video file.
        var json = """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "visibility": "private",
                    "userId": "%s"
                }""";
        var metadata = new MockMultipartFile("metadata", null, "application/json",
                json.formatted(user.getId()).getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Required part 'videoFile' is not present.")));

        // Given missing title metadata.
        json = """
                {
                    "description": "Video 3 description",
                    "visibility": "private",
                    "userId": "%s"
                }""";
        metadata = new MockMultipartFile("metadata", null, "application/json",
                json.formatted(user.getId()).getBytes());
        var videoFileTest = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", "data".getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Video title is required")));

        // Given missing visibility.
        json = """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "userId": "%s"
                }""";
        metadata = new MockMultipartFile("metadata", null, "application/json",
                json.formatted(user.getId()).getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Visibility is required")));

        // Given missing user ID.
        json = """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "visibility": "private"
                }""";
        metadata = new MockMultipartFile("metadata", null, "application/json", json.getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User ID is required")));
    }

    @Test
    public void givenInvalidVisibilityLevel_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var user = userRepository.findByEmail("user@gmail.com");
        var videoFileTest = new MockMultipartFile("videoFile", "video.mp4", "video/mp4", "data".getBytes());
        var metadata = new MockMultipartFile("metadata", null, "application/json", """
                {
                    "title": "Video 3",
                    "description": "Video 3 description",
                    "visibility": "privates",
                    "userId": "%s"
                }""".formatted(user.getId()).getBytes());
        mockMvc.perform(multipart("/api/videos")
                        .file(videoFileTest)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Visibility must either 'private' or 'public'.")));
    }
}