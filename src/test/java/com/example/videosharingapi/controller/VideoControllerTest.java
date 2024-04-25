package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.common.TestUtil;
import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.dto.response.ErrorResponse;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
public class VideoControllerTest {

    private @Autowired VideoRepository videoRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired VideoRatingRepository videoRatingRepository;

    private @Autowired TestUtil testUtil;
    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private final String userId = "3f06af63-a93c-11e4-9797-00505690773f";
    private final String videoId = "e65707b4-e9dc-4d40-9a1d-72667570bd6f";

    private final MockMultipartFile mockVideoFile = new MockMultipartFile(
            "videoFile",
            "test.mp4",
            "video/mp4", RandomStringUtils.random(10).getBytes());

    private VideoDto obtainVideoDto() {
        var thumbnailDto = new ThumbnailDto();
        thumbnailDto.setUrl("Video thumbnail URL");
        thumbnailDto.setWidth(100);
        thumbnailDto.setHeight(100);

        var thumbnails = new HashMap<Thumbnail.Type, ThumbnailDto>();
        thumbnails.put(Thumbnail.Type.DEFAULT, thumbnailDto);

        var videoDto = new VideoDto();
        videoDto.setSnippet(VideoDto.Snippet.builder()
                .title("Video title")
                .description("Video description")
                .duration(Duration.ofSeconds(1000))
                .publishedAt(LocalDateTime.now())
                .thumbnails(thumbnails)
                .videoUrl("Video thumbnail URL")
                .hashtags(List.of("music", "pop"))
                .userId(UUID.fromString(userId))
                .build());
        videoDto.setStatus(VideoDto.Status.builder()
                .privacy("private")
                .commentAllowed(true)
                .madeForKids(false)
                .ageRestricted(false)
                .build());
        return videoDto;
    }

    @Test
    public void givenVideoId_whenGetVideo_thenReturnExpectedVideo() throws Exception {
        var response = new AtomicReference<VideoDto>();
        mockMvc.perform(get("/api/v1/videos")
                        .param("videoId", videoId))
                .andDo(result -> testUtil.toDto(result, response, VideoDto.class))
                .andExpect(status().isOk());

        assertThat(response.get().getSnippet().getTitle())
                .isEqualTo("Video 3");
        assertThat(response.get().getSnippet().getUserId())
                .isEqualTo(UUID.fromString("a05990b1-9110-40b1-aa4c-03951b0705de"));
        assertThat(response.get().getSnippet().getThumbnails())
                .hasSize(1);
        assertThat(response.get().getSnippet().getThumbnails().get(Thumbnail.Type.DEFAULT).getUrl())
                .isEqualTo("Video 3 default thumbnail");
    }

    @Test
    @Transactional
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenReturnSuccessful() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var response = new AtomicReference<VideoDto>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, response, VideoDto.class))
                .andExpect(status().isCreated());

        assertThat(response.get().getSnippet().getTitle()).isEqualTo("Video title");
    }

    @Test
    @Transactional
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenAssertDatabaseIsUpdated() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var response = new AtomicReference<VideoDto>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, response, VideoDto.class))
                .andExpect(status().isCreated());

        // Assert Video is created.
        var video = videoRepository.findById(response.get().getId());
        assertThat(video).isPresent();
        assertThat(videoRepository.findAll()).hasSize(4);

        // Assert VideoStatistic is created.
        var videoStatistic = videoStatisticRepository.findById(video.get().getId());
        assertThat(videoStatistic).isPresent();

        // Assert Thumbnails is created.
        var thumbnails = thumbnailRepository.findAllByVideoId(video.get().getId());
        assertThat(thumbnails).hasSize(1);
        assertThat(thumbnails.get(0).getType()).isEqualTo(Thumbnail.Type.DEFAULT);

        // Assert Hashtags is created.
        var hashtags = hashtagRepository.findAll();
        assertThat(hashtags.stream().map(Hashtag::getTag))
                .containsExactlyInAnyOrder("music", "pop", "sport");
    }

    @Test
    @Transactional
    public void givenInvalidUserId_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setUserId(UUID.randomUUID());
        var metadata = new MockMultipartFile(
                "metadata",
                null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage()).isEqualTo("ID does not exist.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingUserId_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setUserId(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("User ID is required.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingVideoFile_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Required part 'videoFile' is not present.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingVideoTitle_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setTitle(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Video title is required.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingPrivacy_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getStatus().setPrivacy(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Privacy is required.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenInvalidPrivacyStatus_whenPostVideo_thenReturnErrorResponse() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getStatus().setPrivacy("privates");
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var errorResponse = new AtomicReference<ErrorResponse>();
        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andDo(result -> testUtil.toDto(result, errorResponse, ErrorResponse.class))
                .andExpect(status().isBadRequest());

        assertThat(errorResponse.get().getErrorMessage())
                .isEqualTo("Privacy must either 'private' or 'public'.");
        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    public void givenUserId_whenGetVideosByAllCategories_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<VideoDto[]>();
        mockMvc.perform(get("/api/v1/videos/category/all")
                        .param("userId", userId))
                .andDo(result -> testUtil.toDto(result, response, VideoDto[].class))
                .andExpect(status().isOk());
        assertThat(response.get()).hasSize(1);
        assertThat(response.get()[0].getId())
                .isEqualTo(UUID.fromString("e65707b4-e9dc-4d40-9a1d-72667570bd6f"));
    }

    @Test
    public void givenVideoIdAndUserId_whenGetRating_thenReturnExpectedRatingResponse() throws Exception {
        // When there is no VideoRating.
        var response = new AtomicReference<VideoRatingDto>();
        mockMvc.perform(get("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId))
                .andDo(result -> testUtil.toDto(result, response, VideoRatingDto.class))
                .andExpect(status().isOk());
        assertThat(response.get().getRating()).isEqualTo(VideoRatingDto.NONE);
        assertThat(response.get().getPublishedAt()).isNull();

        // When there is VideoRating with LIKE type.
        mockMvc.perform(get("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", "37b32dc2-b0e0-45ab-8469-1ad89a90b978"))
                .andDo(result -> testUtil.toDto(result, response, VideoRatingDto.class))
                .andExpect(status().isOk());
        assertThat(response.get().getRating()).isEqualTo(VideoRatingDto.LIKE);
        assertThat(response.get().getPublishedAt()).isNotNull();

        // When there is VideoRating with DISLIKE type.
        mockMvc.perform(get("/api/v1/videos/rate")
                        .param("userId", "a05990b1-9110-40b1-aa4c-03951b0705de")
                        .param("videoId", "f7d9b74b-750c-4f49-8340-5bcb8450ae14"))
                .andDo(result -> testUtil.toDto(result, response, VideoRatingDto.class))
                .andExpect(status().isOk());
        assertThat(response.get().getRating()).isEqualTo(VideoRatingDto.DISLIKE);
        assertThat(response.get().getPublishedAt()).isNotNull();
    }

    @Test
    @Transactional
    public void givenVideoIdAndUserId_whenRateVideo_thenVideoRatingUpdatedAsExpected() throws Exception {
        // Rate NONE while there is no VideoRating then ignore.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isNoContent());
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(
                UUID.fromString(userId),
                UUID.fromString(videoId));
        assertThat(videoRating).isNull();

        // Rate LIKE then VideoRating is created with LIKE type.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isNoContent());
        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                UUID.fromString(userId),
                UUID.fromString(videoId));
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.LIKE);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "dislike"))
                .andExpect(status().isNoContent());
        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                UUID.fromString(userId),
                UUID.fromString(videoId));
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.DISLIKE);

        // Rate NONE while there is a VideoRating then delete it.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isNoContent());
        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                UUID.fromString(userId),
                UUID.fromString(videoId));
        assertThat(videoRating).isNull();
    }

    @Test
    @Transactional
    public void givenVideoIdAndUserId_whenRateVideo_thenVideoStatisticUpdatedAsExpected() throws Exception {
        // Rate NONE while there is no VideoRating then ignore.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isNoContent());
        var videoStat = videoStatisticRepository.findById(UUID.fromString(videoId));
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate LIKE then VideoRating is created with LIKE type.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isNoContent());
        videoStat = videoStatisticRepository.findById(UUID.fromString(videoId));
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "dislike"))
                .andExpect(status().isNoContent());
        videoStat = videoStatisticRepository.findById(UUID.fromString(videoId));
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate NONE while there is a VideoRating then delete it.
        mockMvc.perform(post("/api/v1/videos/rate")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isNoContent());
        videoStat = videoStatisticRepository.findById(UUID.fromString(videoId));
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void givenUserIdAndVideoId_whenGetRelatedVideos_thenReturnSuccessful() throws Exception {
        var response = new AtomicReference<VideoDto[]>();
        mockMvc.perform(get("/api/v1/videos/related")
                        .param("videoId", videoId)
                        .param("userId", userId))
                .andDo(result -> testUtil.toDto(result, response, VideoDto[].class))
                .andExpect(status().isOk());
        assertThat(response.get()).hasSize(1);
    }
}