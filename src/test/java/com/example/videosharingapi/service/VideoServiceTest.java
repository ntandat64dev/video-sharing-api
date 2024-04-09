package com.example.videosharingapi.service;

import com.example.videosharingapi.model.entity.CommentRating;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS, config = @SqlConfig(commentPrefix = "#"))
@Sql(scripts = "/sql/clean-up.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS, config = @SqlConfig(commentPrefix = "#"))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VideoServiceTest {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoSpecRepository videoSpecRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired CommentRepository commentRepository;
    private @Autowired CommentRatingRepository commentRatingRepository;
    private @Autowired VideoService videoService;

    private User user;

    @BeforeEach
    public void setupUser() {
        user = userRepository.findByEmail("user@gmail.com");
    }

    private VideoDto.VideoDtoBuilder createVideoDtoBuilder() {
        return VideoDto.builder()
                .title("Video title")
                .description("Video description")
                .durationSec(1000)
                .uploadDate(LocalDateTime.now())
                .thumbnailUrl("Video thumbnail URL")
                .videoUrl("Video thumbnail URL")
                .hashtags(Set.of("music", "pop"))
                .userId(user.getId())
                .visibility("private")
                .isCommentAllowed(true)
                .isMadeForKids(false)
                .isAgeRestricted(false);
    }

    @Test
    @Order(1)
    public void givenVideoService_whenGetAllVideos_thenReturnAllVideos() {
        var videos = videoService.getAllVideos();
        assertThat(videos).hasSize(3);
    }

    @Test
    @Order(2)
    public void givenUserId_whenGetVideosByUserId_thenReturnFoundVideos() {
        var foundVideos = videoService.getRecommendVideos(user.getId());
        assertThat(foundVideos).hasSize(2);
    }

    @Test
    public void givenVideoService_whenGetVideo_thenReturnExpectedVideo() {
        Optional<VideoDto> video = videoService.getAllVideos().stream().filter(videoDto -> videoDto.getTitle().equals("Video 2")).findFirst();
        assertThat(video).isPresent();
        assertThat(video.get().getTitle()).isEqualTo("Video 2");
        assertThat(video.get().getDescription()).isEqualTo("Video 2 description");
        assertThat(video.get().getThumbnailUrl()).isEqualTo("Video 2 thumbnail URL");
        assertThat(video.get().getVideoUrl()).isEqualTo("Video 2 video URL");
        assertThat(video.get().getDurationSec()).isEqualTo(2000);
        assertThat(video.get().getUploadDate()).isEqualTo(LocalDateTime.parse("2024-04-02T09:00:00"));
        assertThat(video.get().getVisibility()).isEqualTo("public");
        assertThat(video.get().getHashtags()).hasSize(0);
        assertThat(video.get().getIsAgeRestricted()).isFalse();
        assertThat(video.get().getIsMadeForKids()).isFalse();
        assertThat(video.get().getIsCommentAllowed()).isTrue();
        assertThat(video.get().getLocation()).isEqualTo("Ha Noi, Vietnam");
        assertThat(video.get().getUserId()).isEqualTo(user.getId());
        assertThat(video.get().getSpec().commentCount()).isEqualTo(0);
        assertThat(video.get().getSpec().viewCount()).isEqualTo(0);
        assertThat(video.get().getSpec().likeCount()).isEqualTo(0);
        assertThat(video.get().getSpec().dislikeCount()).isEqualTo(0);
        assertThat(video.get().getSpec().downloadCount()).isEqualTo(0);
    }

    @Test
    @Order(3)
    public void givenVideoDtoObject_whenSave_Video_thenReturnSavedVideoAndVideoSpecIsCreated() {
        var videoDto = createVideoDtoBuilder().build();
        videoService.saveVideo(videoDto);
        var videos = videoRepository.findAllByUserId(user.getId());
        assertThat(videos).hasSize(3);
        var savedVideo = videos.stream().filter(video -> video.getTitle().equals("Video title")).findFirst();
        assertThat(savedVideo).isPresent();
    }

    @Test
    @Order(4)
    public void givenVideoDtoObject_whenSave_Video_thenVideoSpecIsAlsoSaved() {
        var videoDto = createVideoDtoBuilder().build();
        var savedVideo = videoService.saveVideo(videoDto);
        var videoSpec = videoSpecRepository.findById(savedVideo.getId());
        assertThat(videoSpec).isPresent();
    }

    @Test
    public void givenVideoDtoObjectWithNullTitle_whenSave_Video_thenReturnError() {
        var videoDto = createVideoDtoBuilder()
                .title(null)
                .build();
        assertThrows(Exception.class, () -> videoService.saveVideo(videoDto));
    }

    @Test
    @Order(1)
    public void givenViewRequest_whenPostView_thenVideoSpecUpdatedAsExpected() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        var viewRequest = new ViewRequest();
        viewRequest.setVideoId(video.getId());
        viewRequest.setUserId(user.getId());
        viewRequest.setDuration(60);
        viewRequest.setViewedAt(LocalDateTime.now());
        videoService.viewVideo(viewRequest);
        var videoSpec = videoSpecRepository.findById(video.getId());
        assertThat(videoSpec).isPresent();
        assertThat(videoSpec.get().getViewCount()).isEqualTo(1);
        assertThat(videoSpec.get().getLikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDislikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getCommentCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Order(2)
    public void givenRatingRequestWithLikeType_whenRateVideo_thenVideoSpecUpdatedAsExpected() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(video.getId());
        ratingRequest.setRatedAt(LocalDateTime.now());
        ratingRequest.setRating(RatingRequest.RatingType.LIKE);

        videoService.rateVideo(ratingRequest);

        var videoSpec = videoSpecRepository.findById(video.getId());
        assertThat(videoSpec).isPresent();
        assertThat(videoSpec.get().getViewCount()).isEqualTo(1);
        assertThat(videoSpec.get().getLikeCount()).isEqualTo(1);
        assertThat(videoSpec.get().getDislikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getCommentCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Order(3)
    public void givenRatingRequestWithDislikeType_whenRateVideo_thenVideoSpecUpdatedAsExpected() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(video.getId());
        ratingRequest.setRatedAt(LocalDateTime.now());
        ratingRequest.setRating(RatingRequest.RatingType.DISLIKE);

        videoService.rateVideo(ratingRequest);

        var videoSpec = videoSpecRepository.findById(video.getId());
        assertThat(videoSpec).isPresent();
        assertThat(videoSpec.get().getViewCount()).isEqualTo(1);
        assertThat(videoSpec.get().getLikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDislikeCount()).isEqualTo(1);
        assertThat(videoSpec.get().getCommentCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Order(4)
    public void givenRatingRequestWithNoneType_whenRateVideo_thenVideoSpecUpdatedAsExpected() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        var ratingRequest = new RatingRequest();
        ratingRequest.setUserId(user.getId());
        ratingRequest.setVideoId(video.getId());
        ratingRequest.setRatedAt(LocalDateTime.now());
        ratingRequest.setRating(RatingRequest.RatingType.NONE);

        videoService.rateVideo(ratingRequest);

        var videoSpec = videoSpecRepository.findById(video.getId());
        assertThat(videoSpec).isPresent();
        assertThat(videoSpec.get().getViewCount()).isEqualTo(1);
        assertThat(videoSpec.get().getLikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDislikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getCommentCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Order(5)
    public void givenVideo_whenComment_thenVideoSpecUpdatedAsExpected() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        videoService.comment(video.getId(), user.getId(), "Good video");
        var videoSpec = videoSpecRepository.findById(video.getId());
        assertThat(videoSpec).isPresent();
        assertThat(videoSpec.get().getViewCount()).isEqualTo(1);
        assertThat(videoSpec.get().getLikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getDislikeCount()).isEqualTo(0);
        assertThat(videoSpec.get().getCommentCount()).isEqualTo(1);
        assertThat(videoSpec.get().getDownloadCount()).isEqualTo(0);
    }

    @Test
    @Order(6)
    public void giveCommentLikeObject_whenRateComment_thenReturnExpectedCommentRatingObject() {
        var video = videoRepository.findByTitle("Video 1").get(0);
        var comment = commentRepository.findByVideoId(video.getId()).get(0);
        videoService.rateComment(comment.getId(), user.getId(), true);
        var commentRating = commentRatingRepository.findAll().get(0);
        assertThat(commentRating.getComment().getId()).isEqualTo(comment.getId());
        assertThat(commentRating.getUser().getId()).isEqualTo(user.getId());
        assertThat(commentRating.getRating()).isEqualTo(CommentRating.Rating.LIKE);
    }
}
