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
    public void giveVideoService_whenGetAllVideos_thenReturnAllVideos() {
        var videos = videoService.getAllVideos();
        assertThat(videos).hasSize(2);
    }

    @Test
    public void givenVideoDtoObject_whenSave_thenReturnSavedVideoAndVideoSpecIsCreated() {
        var user = userRepository.findByEmail("user@gmail.com");
        var tags = new HashSet<String>();
        tags.add("sport");
        var videoDto = VideoDto.builder()
                .title("Video title")
                .description("Video description")
                .durationSec(1000)
                .uploadDate(LocalDateTime.now())
                .thumbnailUrl("Video thumbnail URL")
                .videoUrl("Video thumbnail URL")
                .tags(tags)
                .userId(user.getId())
                .visibility("private")
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
