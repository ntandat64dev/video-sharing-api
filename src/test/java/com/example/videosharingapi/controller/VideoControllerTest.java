package com.example.videosharingapi.controller;

import com.example.videosharingapi.common.TestSql;
import com.example.videosharingapi.dto.CategoryDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.entity.Hashtag;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.VideoRating;
import com.example.videosharingapi.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestSql
@WithUserDetails("user1")
public class VideoControllerTest {

    private @Autowired VideoRepository videoRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired VideoRatingRepository videoRatingRepository;
    private @Autowired ViewHistoryRepository viewHistoryRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired CommentRatingRepository commentRatingRepository;
    private @Autowired PlaylistItemRepository playlistItemRepository;

    private @Autowired MockMvc mockMvc;
    private @Autowired ObjectMapper objectMapper;

    private final MockMultipartFile createMockVideoFile = new MockMultipartFile(
            "videoFile",
            "test.mp4",
            "video/mp4", RandomStringUtils.random(10).getBytes());

    private VideoDto obtainVideoDto() {
        var videoDto = new VideoDto();
        videoDto.setSnippet(VideoDto.Snippet.builder()
                .title("Video title")
                .description("Video description")
                .hashtags(List.of("music", "pop"))
                .userId("a05990b1")
                .category(new CategoryDto("c0f3f41e"))
                .build());
        videoDto.setStatus(VideoDto.Status.builder()
                .privacy("private")
                .commentAllowed(true)
                .madeForKids(false)
                .ageRestricted(false)
                .build());
        return videoDto;
    }

    private VideoDto obtainVideoDtoForUpdate() {
        var videoDto = new VideoDto();
        videoDto.setId("37b32dc2");
        videoDto.setSnippet(VideoDto.Snippet.builder()
                .title("Video 1 updated")
                .description("Video 1 description updated")
                .publishedAt(LocalDateTime.parse("2030-01-01T09:00:00"))
                .videoUrl("Video 1 video URL updated")
                .userId("a05990b1")
                .duration(Duration.ofSeconds(9000))
                .category(new CategoryDto("f2fe0cb6"))
                .location("US")
                .hashtags(List.of("art"))
                .build());
        videoDto.setStatus(VideoDto.Status.builder()
                .privacy("public")
                .ageRestricted(false)
                .commentAllowed(false)
                .madeForKids(true)
                .build());
        return videoDto;
    }

    @Test
    @WithUserDetails("admin")
    public void whenGetAllVideosWithRoleAdmin_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    @WithUserDetails("user1")
    public void whenGetAllVideosWithRoleUser_thenForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/videos"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenGetMyVideos_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void givenVideoId_whenGetVideo_thenReturnExpectedVideo() throws Exception {
        mockMvc.perform(get("/api/v1/videos/{videoId}", "f7d9b74b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title").value("Video 2"))
                .andExpect(jsonPath("$.snippet.userId").value("a05990b1"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(1))
                .andExpect(jsonPath("$.snippet.thumbnails.DEFAULT.url")
                        .value("Video 2 default thumbnail URL"));
    }

    @Test
    @Transactional
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenSuccess() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(createMockVideoFile)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.snippet.title").value("Video title"))
                .andExpect(jsonPath("$.snippet.duration").value("PT16M40S"))
                .andExpect(jsonPath("$.snippet.userId").value("a05990b1"))
                .andExpect(jsonPath("$.snippet.category.category").value("Sports"));
    }

    @Test
    @Transactional
    public void givenVideoDtoAndMockVideoFile_whenPostVideo_thenAssertDatabaseIsUpdated() throws Exception {
        var videoDto = obtainVideoDto();
        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        var result = mockMvc.perform(multipart("/api/v1/videos")
                        .file(createMockVideoFile)
                        .file(metadata))
                .andExpect(status().isCreated())
                .andReturn();

        // Assert Video is created.
        assertThat(videoRepository.findAll()).hasSize(4);
        var video = videoRepository
                .findById(JsonPath.read(result.getResponse().getContentAsString(), "$.id"));
        assertThat(video).isPresent();

        // Assert VideoStatistic is created.
        var videoStatistic = videoStatisticRepository.findById(video.get().getId());
        assertThat(videoStatistic).isPresent();

        // Assert Thumbnails is created.
        var thumbnails = thumbnailRepository.findAllByVideoId(video.get().getId());
        assertThat(thumbnails).hasSize(1);
        assertThat(thumbnails.getFirst().getType()).isEqualTo(Thumbnail.Type.DEFAULT);

        // Assert Hashtags is created.
        assertThat(hashtagRepository.findAllTag()).containsExactlyInAnyOrder("music", "pop", "sport");
    }

    @Test
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
    public void givenMissingMetadata_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().setTitle(null);
        videoDto.getSnippet().setUserId(null);
        videoDto.getSnippet().setCategory(null);
        videoDto.getStatus().setPrivacy(null);

        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(createMockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors")
                        .value(containsInAnyOrder(
                                "snippet.userId: must not be null",
                                "snippet.title: must not be blank",
                                "snippet.category: must not be null",
                                "status.privacy: must not be blank")));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    public void givenInvalidMetadata_whenPostVideo_thenError() throws Exception {
        var videoDto = obtainVideoDto();
        videoDto.getSnippet().getCategory().setId(UUID.randomUUID().toString());
        videoDto.getStatus().setPrivacy("privates");
        videoDto.getSnippet().setUserId(UUID.randomUUID().toString());

        var metadata = new MockMultipartFile(
                "metadata", null,
                "application/json", objectMapper.writeValueAsBytes(videoDto));

        mockMvc.perform(multipart("/api/v1/videos")
                        .file(createMockVideoFile)
                        .file(metadata))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors")
                        .value(containsInAnyOrder(
                                "snippet.category.id: ID does not exist.",
                                "status.privacy: must match \"(?i)(private|public)\"",
                                "snippet.userId: ID does not exist.")));

        // Assert video is not saved.
        assertThat(videoRepository.count()).isEqualTo(3);
    }

    @Test
    @Transactional
    public void givenVideoDto_whenUpdate_thenSuccess() throws Exception {
        var videoDto = obtainVideoDtoForUpdate();
        mockMvc.perform(put("/api/v1/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.snippet.title").value("Video 1 updated"))
                .andExpect(jsonPath("$.snippet.description")
                        .value("Video 1 description updated"))
                .andExpect(jsonPath("$.snippet.publishedAt").value("2024-04-01T09:00:00"))
                .andExpect(jsonPath("$.snippet.thumbnails.length()").value(2))
                .andExpect(jsonPath("$.snippet.thumbnails.DEFAULT.url")
                        .value("Video 1 default thumbnail URL"))
                .andExpect(jsonPath("@.snippet.category.id").value("f2fe0cb6"))
                .andExpect(jsonPath("@.snippet.category.category").value("Comedy"))
                .andExpect(jsonPath("$.snippet.userId").value("a05990b1"))
                .andExpect(jsonPath("$.snippet.username").value("user1"))
                .andExpect(jsonPath("$.snippet.userImageUrl")
                        .value("User 1 default thumbnail URL"))
                .andExpect(jsonPath("$.snippet.videoUrl").value("Video 1 URL"))
                .andExpect(jsonPath("$.snippet.hashtags").value(contains("art")))
                .andExpect(jsonPath("$.snippet.duration").value("PT16M40S"))
                .andExpect(jsonPath("$.snippet.location").value("US"))
                .andExpect(jsonPath("$.status.privacy").value("PUBLIC"))
                .andExpect(jsonPath("$.status.ageRestricted").value(false))
                .andExpect(jsonPath("$.status.commentAllowed").value(false))
                .andExpect(jsonPath("$.status.madeForKids").value(true))
                .andExpect(jsonPath("$.statistic.viewCount").value(4))
                .andExpect(jsonPath("$.statistic.likeCount").value(2))
                .andExpect(jsonPath("$.statistic.dislikeCount").value(0))
                .andExpect(jsonPath("$.statistic.commentCount").value(1))
                .andExpect(jsonPath("$.statistic.downloadCount").value(0));
    }

    @Test
    @Transactional
    public void givenVideoDto_whenUpdate_thenDatabaseIsUpdated() throws Exception {
        var videoDto = obtainVideoDtoForUpdate();
        mockMvc.perform(put("/api/v1/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoDto)))
                .andExpect(status().isOk());

        // Assert Video is updated.
        var video = videoRepository.findById("37b32dc2").orElseThrow();
        assertThat(video.getTitle()).isEqualTo("Video 1 updated");
        assertThat(video.getDescription()).isEqualTo("Video 1 description updated");
        // publishedAt not updated as expect.
        assertThat(video.getPublishedAt()).isEqualTo(LocalDateTime.parse("2024-04-01T09:00:00"));
        // videoUrl not updated as expect.
        assertThat(video.getVideoUrl()).isEqualTo("Video 1 URL");
        assertThat(video.getCategory().getId()).isEqualTo("f2fe0cb6");
        assertThat(video.getLocation()).isEqualTo("US");
        // thumbnails not updated as expect.
        assertThat(video.getThumbnails()).hasSize(2);
        assertThat(video.getThumbnails().stream().map(Thumbnail::getUrl)).containsExactlyInAnyOrder(
                "Video 1 default thumbnail URL",
                "Video 1 medium thumbnail URL");
        assertThat(video.getHashtags().stream().map(Hashtag::getTag)).containsExactlyInAnyOrder("art");
        // duration not updated as expect.
        assertThat(video.getDurationSec()).isEqualTo(1000);
        assertThat(video.getPrivacy().getId()).isEqualTo("f01121d2");
        assertThat(video.getAgeRestricted()).isEqualTo(false);
        assertThat(video.getCommentAllowed()).isEqualTo(false);
        assertThat(video.getMadeForKids()).isEqualTo(true);
        assertThat(video.getUser().getId()).isEqualTo("a05990b1");

        // Assert Thumbnail is not updated.
        assertThat(thumbnailRepository.findAllByVideoId("37b32dc2")).hasSize(2);
        assertThat(thumbnailRepository.findAllByVideoId("37b32dc2").stream().map(Thumbnail::getType))
                .containsExactlyInAnyOrder(Thumbnail.Type.DEFAULT, Thumbnail.Type.MEDIUM);

        // Assert VideoStatistic is not updated.
        var videoStatistic = videoStatisticRepository.findById("37b32dc2").orElseThrow();
        assertThat(videoStatistic.getViewCount()).isEqualTo(4);
        assertThat(videoStatistic.getLikeCount()).isEqualTo(2);
        assertThat(videoStatistic.getDislikeCount()).isEqualTo(0);
        assertThat(videoStatistic.getCommentCount()).isEqualTo(1);
        assertThat(videoStatistic.getDownloadCount()).isEqualTo(0);

        // Assert old hashtags is retained and new hashtags is added.
        assertThat(hashtagRepository.findAllTag()).containsExactlyInAnyOrder("music", "sport", "art");
    }

    @Test
    @Transactional
    public void givenInvalidVideoDto_whenUpdate_thenError() throws Exception {
        var videoDto = obtainVideoDtoForUpdate();
        videoDto.getSnippet().setTitle("");
        videoDto.getSnippet().setUserId("12345678");
        videoDto.getSnippet().setCategory(new CategoryDto("12345678"));

        mockMvc.perform(put("/api/v1/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors")
                        .value(containsInAnyOrder(
                                "snippet.title: must not be blank",
                                "snippet.userId: ID does not exist.",
                                "snippet.category.id: ID does not exist.")));
    }

    @Test
    @Transactional
    @WithUserDetails("user2")
    public void givenVideoDtoAndInvalidUser_whenUpdateWith_thenError() throws Exception {
        var videoDto = obtainVideoDtoForUpdate();

        mockMvc.perform(put("/api/v1/videos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(videoDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void givenVideoId_whenDeleteVideo_thenSuccess() throws Exception {
        mockMvc.perform(delete("/api/v1/videos")
                        .param("id", "37b32dc2"))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void givenVideoId_whenDeleteVideo_thenDatabaseIsUpdated() throws Exception {
        mockMvc.perform(delete("/api/v1/videos")
                        .param("id", "37b32dc2"))
                .andExpect(status().isNoContent());

        // Assert Video is deleted.
        assertThat(videoRepository.findById("37b32dc2")).isNotPresent();

        // Assert Thumbnails is deleted.
        assertThat(thumbnailRepository.count()).isEqualTo(6);
        assertThat(thumbnailRepository.findAllById(List.of("b2825704", "2c48d7cd"))).isEmpty();

        // Assert VideoStatistic is deleted.
        assertThat(videoStatisticRepository.findById("37b32dc2")).isNotPresent();

        // Assert Hashtags is retained.
        assertThat(hashtagRepository.findAllTag()).containsExactlyInAnyOrder("music", "sport");

        // Assert VideoRating is deleted.
        assertThat(videoRatingRepository.count()).isEqualTo(2);
        assertThat(videoRatingRepository.findByVideoId("37b32dc2")).isEmpty();

        // Assert ViewHistory is deleted.
        assertThat(viewHistoryRepository.count()).isEqualTo(5);
        assertThat(viewHistoryRepository.findAllByVideoId("37b32dc2")).isEmpty();

        // Assert Comment is deleted.
        assertThat(commentRepository.count()).isEqualTo(2);
        assertThat(commentRepository.findAllByVideoId("37b32dc2")).isEmpty();
        assertThat(commentRatingRepository.count()).isEqualTo(2);
        assertThat(commentRatingRepository.findAllByCommentVideoId("37b32dc2")).isEmpty();

        // Assert PlaylistItem is deleted.
        assertThat(playlistItemRepository.count()).isEqualTo(2);
        assertThat(playlistItemRepository.findAllByPlaylistId("236e2aa6")).isEmpty();
    }

    @Test
    public void givenVideoIdThatNotExists_whenDeleteVideo_thenError() throws Exception {
        mockMvc.perform(delete("/api/v1/videos")
                        .param("id", "12345678"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]")
                        .value("id: ID does not exist."));
    }

    @Test
    public void givenVideoIdAndInvalidUser_whenDeleteVideo_thenError() throws Exception {
        mockMvc.perform(delete("/api/v1/videos")
                        .param("id", "e65707b4"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenGetVideosByCategoryAll_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/category/all/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*].id").value(contains("e65707b4")));
    }

    @Test
    public void givenVideoId_whenGetRating_thenReturnExpectedRating() throws Exception {
        // When there is no VideoRating.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("videoId", "f7d9b74b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE))
                .andExpect(jsonPath("$.publishedAt").doesNotExist());

        // When there is VideoRating with LIKE type.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("videoId", "37b32dc2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.LIKE))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());

        // When there is VideoRating with DISLIKE type.
        mockMvc.perform(get("/api/v1/videos/rate/mine")
                        .param("videoId", "e65707b4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.DISLIKE))
                .andExpect(jsonPath("$.publishedAt").isNotEmpty());
    }

    @Test
    @Transactional
    public void givenVideoId_whenRateVideo_thenDatabaseIsUpdated() throws Exception {
        final var userId = "a05990b1";
        final var videoId = "f7d9b74b";

        // Rate NONE while there is no VideoRating then ignore.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE));

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating).isNull();
        var videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate LIKE then VideoRating is created with LIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("videoId", videoId)
                        .param("rating", "like"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.LIKE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.LIKE);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("videoId", videoId)
                        .param("rating", "dislike"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.DISLIKE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.DISLIKE);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(2);

        // Rate NONE while there is a VideoRating then delete it.
        mockMvc.perform(post("/api/v1/videos/rate/mine")
                        .param("videoId", videoId)
                        .param("rating", "none"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value(VideoRatingDto.NONE));

        videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating).isNull();
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);
    }

    @Test
    public void givenVideoId_whenGetRelatedVideos_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/related/mine")
                        .param("videoId", "e65707b4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*].id").value(contains("e65707b4")));
    }

    @Test
    public void whenGetVideoCategories_thenSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/videos/video-categories/mine"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(contains("music")));
    }
}