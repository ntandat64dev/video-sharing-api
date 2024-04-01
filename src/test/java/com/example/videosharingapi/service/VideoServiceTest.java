package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.repository.VideoSpecRepository;
import com.example.videosharingapi.testutil.InsertDataExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({ "dev", "test" })
@ExtendWith(InsertDataExtension.class)
public class VideoServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoSpecRepository videoSpecRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoService videoService;

    @Test
    public void whenGetAllVideos_thenReturnAllVideos() {
        var videos = videoService.getAllVideos();
        assertThat(videos).hasSize(2);
    }

    @Test
    public void givenUserId_whenGetVideosByUserId_thenReturnFoundVideos() {
        var userId = userRepository.findByEmail("user@gmail.com").getId();
        var foundVideos = videoService.getRecommendVideos(userId);
        assertThat(foundVideos).hasSize(2);
    }

    @Test
    public void whenGetAVideo_thenCheckInfo() {
        var userId = userRepository.findByEmail("user@gmail.com").getId();
        Optional<VideoDto> video = videoService.getAllVideos().stream().filter(videoDto -> videoDto.getTitle().equals("Video 2")).findFirst();
        assertThat(video).isPresent();

        assertThat(video.get().getTitle()).isEqualTo("Video 2");
        assertThat(video.get().getDescription()).isEqualTo("Video 2 description");
        assertThat(video.get().getThumbnailUrl()).isEqualTo("Video 2 thumbnail URL");
        assertThat(video.get().getVideoUrl()).isEqualTo("Video 2 video URL");
        assertThat(video.get().getDurationSec()).isEqualTo(2000);
        assertThat(video.get().getUploadDate()).isEqualTo(LocalDateTime.parse("2024-04-05T09:00:00"));
        assertThat(video.get().getVisibility()).isEqualTo("public");
        assertThat(video.get().getHashtags()).hasSize(1);
        assertThat(video.get().getHashtags().stream().toList().get(0)).isEqualTo("music");
        assertThat(video.get().getIsAgeRestricted()).isFalse();
        assertThat(video.get().getIsMadeForKids()).isFalse();
        assertThat(video.get().getIsCommentAllowed()).isTrue();
        assertThat(video.get().getLocation()).isEqualTo("Vietnam");
        assertThat(video.get().getUserId()).isEqualTo(userId);
        assertThat(video.get().getSpec().commentCount()).isEqualTo(0);
        assertThat(video.get().getSpec().viewCount()).isEqualTo(0);
        assertThat(video.get().getSpec().likeCount()).isEqualTo(0);
        assertThat(video.get().getSpec().dislikeCount()).isEqualTo(0);
        assertThat(video.get().getSpec().downloadCount()).isEqualTo(0);
    }

    @Test
    public void givenVideoDtoObject_whenSave_thenReturnSavedVideoAndVideoSpecIsCreated() {
        var user = userRepository.findByEmail("user@gmail.com");
        var hashtags = new HashSet<String>();
        hashtags.add("sport");
        var videoDto = VideoDto.builder()
                .title("Video title")
                .description("Video description")
                .durationSec(1000)
                .uploadDate(LocalDateTime.now())
                .thumbnailUrl("Video thumbnail URL")
                .videoUrl("Video thumbnail URL")
                .hashtags(hashtags)
                .userId(user.getId())
                .visibility("private")
                .isCommentAllowed(true)
                .isMadeForKids(false)
                .isAgeRestricted(false)
                .build();
        videoService.save(videoDto);
        var videos = videoRepository.findAllByUserId(user.getId());
        assertThat(videos).hasSize(3);
        var savedVideo = videos.stream().filter(video -> video.getTitle().equals("Video title")).findFirst();
        assertThat(savedVideo).isPresent();

        var videoSpec = videoSpecRepository.findById(savedVideo.get().getId());
        assertThat(videoSpec).isPresent();
    }
}
