package com.example.videosharingapi.repository;

import com.example.videosharingapi.config.AuditingConfig;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.Visibility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VideoRepositoryTest {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired VisibilityRepository visibilityRepository;

    @Test
    public void givenVideoObject_whenSave_thenReturnSavedVideo() {

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl("Thumbnail URL");
        thumbnail.setWidth(100);
        thumbnail.setHeight(100);

        var video = Video.builder()
                .title("Video title")
                .thumbnails(List.of(thumbnail))
                .videoUrl("Video URL")
                .uploadDate(LocalDateTime.now())
                .build();

        var visibility = visibilityRepository.findByLevel(Visibility.VisibilityLevel.PUBLIC);
        video.setVisibility(visibility);

        var user = userRepository.getReferenceById(UUID.fromString("9eb456d7-1a59-4efa-9a21-e509bbba5eb4"));
        video.setUser(user);
        video.setDurationSec(1200);

        var savedVideo = videoRepository.save(video);
        assertThat(savedVideo).isNotNull();
    }
}