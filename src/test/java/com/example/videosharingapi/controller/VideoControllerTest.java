package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.dto.CategoryDto;
import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.entity.Hashtag;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.VideoRating;
import com.example.videosharingapi.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user2")
public class VideoControllerTest {

    private @Autowired VideoRepository videoRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired VideoRatingRepository videoRatingRepository;

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private final String userId = "3f06af63";
    private final String videoId = "e65707b4";

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
                .thumbnails(thumbnails)
                .hashtags(List.of("music", "pop"))
                .userId(userId)
                .category(new CategoryDto("8c1f4a20"))
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
    @WithUserDetails("user1")
    public void whenGetAllVideosWithRoleAdmin_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithUserDetails("user2")
    public void whenGetAllVideosWithRoleUser_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void givenVideoId_whenGetVideo_thenReturnExpectedVideo() throws Exception {
        mockMvc.perform(get("/api/v1/videos/{videoId}", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title").value("Video 3"))
                .andExpect(jsonPath("$.snippet.userId")
                        .value("a05990b1"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(1))
                .andExpect(jsonPath("$.snippet.thumbnails.DEFAULT.url")
                        .value("Video 3 default thumbnail"));
    }

    @Test
    @Transactional
    @WithUserDetails("user1")
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenSuccess() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.title").value("Video title"))
                .andExpect(jsonPath("$.snippet.duration").value("PT16M40S"));
    }

    @Test
    @Transactional
    @WithUserDetails("user1")
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenAssertDatabaseIsUpdated() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var result = mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert Video is created.
        var video = videoRepository
                .findById(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
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
    public void givenInvalidUserId_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setUserId(UUID.randomUUID().toString());
        var metadata = new MockMultipartFile(
                "metadata",
                null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.userId: ID does not exist."));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingUserId_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setUserId(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.userId: must not be null"));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingVideoFile_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Required part 'videoFile' is not present."));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingVideoTitle_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setTitle(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.title: must not be blank"));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingVideoCategory_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setCategory(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.category: must not be null"));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenInvalidVideoCategory_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().getCategory().setId(UUID.randomUUID().toString());
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("snippet.category.id: ID does not exist."));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenMissingPrivacy_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getStatus().setPrivacy(null);
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("status.privacy: must not be blank"));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenInvalidPrivacyStatus_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getStatus().setPrivacy("privates");
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(mockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("status.privacy: must match \"(?i)(private|public)\""));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    public void givenUserId_whenGetVideosByAllCategories_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/category/all/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].id")
                        .value(Matchers.containsInAnyOrder("f7d9b74b", "37b32dc2")));
    }

    @Test
    public void givenVideoIdAndUserId_whenGetRating_thenReturnExpectedRatingResponse() throws Exception {
        // When there is no VideoRating.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE))
                .andExpect(jsonPath("$.publishedAt").doesNotExist());

        // When there is VideoRating with LIKE type.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", "37b32dc2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.LIKE))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        // When there is VideoRating with DISLIKE type.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("userId", "a05990b1")
                        .param("videoId", "f7d9b74b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.DISLIKE))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());
    }

    @Test
    @Transactional
    @WithUserDetails("user1")
    public void givenVideoIdAndUserId_whenRateVideo_thenVideoRatingUpdatedAsExpected() throws Exception {
        // Rate NONE while there is no VideoRating then ignore.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE));

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(
                userId,
                videoId);
        assertThat(videoRating).isNull();

        // Rate LIKE then VideoRating is created with LIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.LIKE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                userId,
                videoId);
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.LIKE);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "dislike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.DISLIKE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                userId,
                videoId);
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.DISLIKE);

        // Rate NONE while there is a VideoRating then delete it.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(
                userId,
                videoId);
        assertThat(videoRating).isNull();
    }

    @Test
    @Transactional
    public void givenVideoIdAndUserId_whenRateVideo_thenVideoStatisticUpdatedAsExpected() throws Exception {
        // Rate NONE while there is no VideoRating then ignore.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk());
        var videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate LIKE then VideoRating is created with LIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isOk());
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "dislike"))
                .andExpect(status().isOk());
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate NONE while there is a VideoRating then delete it.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("userId", userId)
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk());
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void givenUserIdAndVideoId_whenGetRelatedVideos_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/related/mine")
                        .param("videoId", videoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithUserDetails("user2")
    public void givenUserId_whenGetVideoCategories_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/video-categories/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$")
                        .value(contains("music", "sport")));
    }
}