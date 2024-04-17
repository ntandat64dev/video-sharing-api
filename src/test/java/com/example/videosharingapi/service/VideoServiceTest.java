package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.ThumbnailDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/data-h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-h2.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS,
        config = @SqlConfig(commentPrefix = "#"))
public class VideoServiceTest {

    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired VideoService videoService;
    private @Autowired VideoRatingRepository videoRatingRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;

    private final UUID userId = UUID.fromString("3f06af63-a93c-11e4-9797-00505690773f");
    private @MockBean MultipartFile videoFile;

    @BeforeEach
    public void setUp() {
        when(videoFile.getSize()).thenReturn(1000L);
    }

    private VideoDto createVideoDto() {
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
                .userId(userId)
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
    @Transactional
    public void givenVideoDtoObject_whenSave_thenAssertVideoIsSaved() {
        var videoDto = createVideoDto();
        videoService.saveVideo(videoFile, videoDto);
        var videos = videoRepository.findAllByUserId(userId);
        assertThat(videos).hasSize(3);
        var savedVideo = videos.stream().filter(video -> video.getTitle().equals("Video title")).findFirst();
        assertThat(savedVideo).isPresent();
    }

    @Test
    @Transactional
    public void givenVideoDtoObject_whenSave_thenVideoThumbnailsIsAlsoSaved() {
        var videoDto = createVideoDto();
        var savedVideo = videoService.saveVideo(videoFile, videoDto);
        var thumbnails = thumbnailRepository.findAllByVideoId(savedVideo.getId());
        assertThat(thumbnails).hasSize(1);
    }

    @Test
    @Transactional
    public void givenVideoDtoObject_whenSave_thenVideoStatisticIsAlsoSaved() {
        var videoDto = createVideoDto();
        var savedVideo = videoService.saveVideo(videoFile, videoDto);
        var videoStat = videoStatisticRepository.findById(savedVideo.getId());
        assertThat(videoStat).isPresent();
    }

    @Test
    @Transactional
    public void givenVideoDtoObject_whenSave_thenHashtagsIsAlsoSaved() {
        var videoDto = createVideoDto();
        videoService.saveVideo(videoFile, videoDto);
        assertThat(hashtagRepository.findAll().stream().map(Hashtag::getTag))
                .containsExactlyInAnyOrder("music", "pop", "sport");
    }

    @Test
    public void givenVideoDtoObjectWithNullTitle_whenSave_thenErrorOccurAndVideoIsNotSaved() {
        var videoDto = createVideoDto();
        videoDto.getSnippet().setTitle(null);
        assertThrows(Exception.class, () -> videoService.saveVideo(videoFile, videoDto));
        assertThat(videoRepository.findAll()).hasSize(3);
    }

    @Test
    public void givenUserId_whenGetVideosByAllCategories_thenReturnSuccessful() {
        var recommendVideos = videoService.getVideosByAllCategories(userId);
        assertThat(recommendVideos).hasSize(2);
    }

    @Test
    @Transactional
    public void givenRatingRequest_whenRateVideo_thenVideoRatingUpdatedAsExpected() {
        var videoId = UUID.fromString("e65707b4-e9dc-4d40-9a1d-72667570bd6f");

        // Rate NONE while there is no VideoRating then ignore.
        videoService.rateVideo(videoId, userId, VideoRatingDto.NONE);
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating).isNull();

        // Rate LIKE then VideoRating is created with LIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.LIKE);
        videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating).isNotNull();
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.LIKE);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.DISLIKE);
        var videoRating2 = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating2).isNotNull();
        assertThat(videoRating2.getId()).isEqualTo(videoRating.getId());
        assertThat(videoRating2.getRating()).isEqualTo(VideoRating.Rating.DISLIKE);

        // Rate NONE while there is a VideoRating then delete it.
        videoService.rateVideo(videoId, userId, VideoRatingDto.NONE);
        videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        assertThat(videoRating).isNull();
    }

    @Test
    @Transactional
    public void givenRatingRequest_whenRateVideo_thenVideoStatisticUpdatedAsExpected() {
        var videoId = UUID.fromString("e65707b4-e9dc-4d40-9a1d-72667570bd6f");

        // Rate NONE while there is no VideoRating then ignore.
        videoService.rateVideo(videoId, userId, VideoRatingDto.NONE);
        var videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate LIKE then VideoRating is created with LIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.LIKE);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.DISLIKE);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate NONE while there is a VideoRating then delete it.
        videoService.rateVideo(videoId, userId, VideoRatingDto.NONE);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void givenVideoIdAndUserId_whenGetRating_thenReturnExpectedRatingResponse() {
        var videoId = UUID.fromString("e65707b4-e9dc-4d40-9a1d-72667570bd6f");

        // When there is no VideoRating.
        var videoRatingDto = videoService.getRating(videoId, userId);
        assertThat(videoRatingDto.getRating()).isEqualTo(VideoRatingDto.NONE);
        assertThat(videoRatingDto.getPublishedAt()).isNull();

        // When there is VideoRating with LIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.LIKE);
        videoRatingDto = videoService.getRating(videoId, userId);
        assertThat(videoRatingDto).isNotNull();
        assertThat(videoRatingDto.getRating()).isEqualTo(VideoRatingDto.LIKE);

        // When update VideoRating to DISLIKE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.DISLIKE);
        videoRatingDto = videoService.getRating(videoId, userId);
        assertThat(videoRatingDto).isNotNull();
        assertThat(videoRatingDto.getRating()).isEqualTo(VideoRatingDto.DISLIKE);

        // When update VideoRating to NONE type.
        videoService.rateVideo(videoId, userId, VideoRatingDto.NONE);
        videoRatingDto = videoService.getRating(videoId, userId);
        assertThat(videoRatingDto.getRating()).isEqualTo(VideoRatingDto.NONE);
        assertThat(videoRatingDto.getPublishedAt()).isNull();
    }

    @Test
    public void givenUserIdAndVideoId_whenGetRelatedVideos_thenReturnSuccessful() {
        var relatedVideos = videoService.
                getRelatedVideos(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"), userId);
        assertThat(relatedVideos).hasSize(2);
    }
}