package com.example.videosharingapi.service;

import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.payload.ThumbnailDto;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.RatingResponse;
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

    private @Autowired UserRepository userRepository;
    private @Autowired VideoStatisticRepository videoStatisticRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired VideoService videoService;
    private @Autowired VideoRatingRepository videoRatingRepository;
    private @Autowired HashtagRepository hashtagRepository;
    private @Autowired ThumbnailRepository thumbnailRepository;

    private User user;
    private @MockBean MultipartFile videoFile;

    @BeforeEach
    public void setUp() {
        user = userRepository.findByEmail("user@gmail.com");
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
                .userId(user.getId())
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
    public void givenVideoId_whenGetVideoById_thenReturnExpectedVideo() {
        var video = videoService.getVideoById(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"));
        assertThat(video.getSnippet().getTitle()).isEqualTo("Video 1");
    }

    @Test
    public void givenUserId_whenGetRecommendVideos_thenReturnExpectedVideos() {
        var recommendVideos = videoService.getRecommendVideos(user.getId());
        assertThat(recommendVideos).hasSize(2);
    }

    @Test
    public void givenUserIdAndVideoId_whenGetRelatedVideos_thenReturnExpectedVideos() {
        var relatedVideos = videoService.
                getRelatedVideos(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"), user.getId());
        assertThat(relatedVideos).hasSize(2);
    }

    @Test
    @Transactional
    public void givenVideoDtoObject_whenSave_thenAssertVideoIsSaved() {
        var videoDto = createVideoDto();
        videoService.saveVideo(videoFile, videoDto);
        var videos = videoRepository.findAllByUserId(user.getId());
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
    @Transactional
    public void givenViewRequest_whenViewVideo_thenVideoStatisticUpdatedAsExpected() {
        var video = videoRepository
                .findById(UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978"))
                .orElseThrow();
        var viewRequest = new ViewRequest();
        viewRequest.setVideoId(video.getId());
        viewRequest.setUserId(user.getId());
        viewRequest.setDuration(60);
        viewRequest.setViewedAt(LocalDateTime.now());
        videoService.viewVideo(viewRequest);
        var videoStat = videoStatisticRepository.findById(video.getId());
        assertThat(videoStat).isPresent();
        assertThat(videoStat.get().getViewCount()).isEqualTo(1);
        assertThat(videoStat.get().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.get().getDislikeCount()).isEqualTo(0);
        assertThat(videoStat.get().getCommentCount()).isEqualTo(0);
        assertThat(videoStat.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void givenRatingRequest_whenRateVideo_thenVideoRatingUpdatedAsExpected() {
        var videoId = UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978");
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(videoId);
        ratingRequest.setRatedAt(LocalDateTime.now());

        // Rate NONE while there is no VideoRating then ignore.
        ratingRequest.setRating(RatingRequest.RatingType.NONE);
        videoService.rateVideo(ratingRequest);
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), videoId);
        assertThat(videoRating).isNull();

        // Rate LIKE then VideoRating is created with LIKE type.
        ratingRequest.setRating(RatingRequest.RatingType.LIKE);
        videoService.rateVideo(ratingRequest);
        videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), videoId);
        assertThat(videoRating).isNotNull();
        assertThat(videoRating.getRating()).isEqualTo(VideoRating.Rating.LIKE);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        ratingRequest.setRating(RatingRequest.RatingType.DISLIKE);
        videoService.rateVideo(ratingRequest);
        var videoRating2 = videoRatingRepository.findByUserIdAndVideoId(user.getId(), videoId);
        assertThat(videoRating2).isNotNull();
        assertThat(videoRating2.getId()).isEqualTo(videoRating.getId());
        assertThat(videoRating2.getRating()).isEqualTo(VideoRating.Rating.DISLIKE);

        // Rate NONE while there is a VideoRating then delete it.
        ratingRequest.setRating(RatingRequest.RatingType.NONE);
        videoService.rateVideo(ratingRequest);
        videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), videoId);
        assertThat(videoRating).isNull();
    }

    @Test
    @Transactional
    public void givenRatingRequest_whenRateVideo_thenVideoStatisticUpdatedAsExpected() {
        var videoId = UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978");
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(videoId);
        ratingRequest.setRatedAt(LocalDateTime.now());

        // Rate NONE while there is no VideoRating then ignore.
        ratingRequest.setRating(RatingRequest.RatingType.NONE);
        videoService.rateVideo(ratingRequest);
        var videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate LIKE then VideoRating is created with LIKE type.
        ratingRequest.setRating(RatingRequest.RatingType.LIKE);
        videoService.rateVideo(ratingRequest);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(1);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);

        // Rate DISLIKE then VideoRating is updated with DISLIKE type.
        ratingRequest.setRating(RatingRequest.RatingType.DISLIKE);
        videoService.rateVideo(ratingRequest);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(1);

        // Rate NONE while there is a VideoRating then delete it.
        ratingRequest.setRating(RatingRequest.RatingType.NONE);
        videoService.rateVideo(ratingRequest);
        videoStat = videoStatisticRepository.findById(videoId);
        assertThat(videoStat.orElseThrow().getLikeCount()).isEqualTo(0);
        assertThat(videoStat.orElseThrow().getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void givenVideoIdAndUserId_whenGetRating_thenReturnExpectedRatingResponse() {
        var videoId = UUID.fromString("37b32dc2-b0e0-45ab-8469-1ad89a90b978");
        var userId = user.getId();

        // When there is no VideoRating.
        var ratingResponse = videoService.getRating(videoId, userId);
        assertThat(ratingResponse.getRating()).isEqualTo(RatingResponse.RatingType.NONE);
        assertThat(ratingResponse.getRatedAt()).isNull();

        // When there is VideoRating with LIKE type.
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(videoId);
        ratingRequest.setRatedAt(LocalDateTime.parse("2024-04-10T12:00:00.123456"));
        ratingRequest.setRating(RatingRequest.RatingType.LIKE);
        videoService.rateVideo(ratingRequest);
        ratingResponse = videoService.getRating(videoId, userId);
        assertThat(ratingResponse).isNotNull();
        assertThat(ratingResponse.getRating()).isEqualTo(RatingResponse.RatingType.LIKE);
        assertThat(ratingResponse.getRatedAt()).isEqualTo(LocalDateTime.parse("2024-04-10T12:00:00.123456"));

        // When update VideoRating to DISLIKE type.
        ratingRequest.setRating(RatingRequest.RatingType.DISLIKE);
        videoService.rateVideo(ratingRequest);
        ratingResponse = videoService.getRating(videoId, userId);
        assertThat(ratingResponse).isNotNull();
        assertThat(ratingResponse.getRating()).isEqualTo(RatingResponse.RatingType.DISLIKE);
        assertThat(ratingResponse.getRatedAt()).isEqualTo(LocalDateTime.parse("2024-04-10T12:00:00.123456"));

        // When update VideoRating to NONE type.
        ratingRequest.setRating(RatingRequest.RatingType.NONE);
        videoService.rateVideo(ratingRequest);
        ratingResponse = videoService.getRating(videoId, userId);
        assertThat(ratingResponse.getRating()).isEqualTo(RatingResponse.RatingType.NONE);
        assertThat(ratingResponse.getRatedAt()).isNull();
    }
}