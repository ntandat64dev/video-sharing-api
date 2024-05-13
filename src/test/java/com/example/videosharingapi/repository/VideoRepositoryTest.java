package com.example.videosharingapi.repository;

import com.example.videosharingapi.config.AuditingConfig;
import com.example.videosharingapi.entity.Privacy;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.Video;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class VideoRepositoryTest {

    private @Autowired UserRepository userRepository;
    private @Autowired VideoRepository videoRepository;
    private @Autowired PrivacyRepository privacyRepository;

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
                .publishedAt(LocalDateTime.now())
                .build();

        var privacy = privacyRepository.findByStatus(Privacy.Status.PUBLIC);
        video.setPrivacy(privacy);

        var user = userRepository.getReferenceById("a05990b1");
        video.setUser(user);
        video.setDurationSec(1200);

        var savedVideo = videoRepository.save(video);
        assertThat(savedVideo).isNotNull();
    }
}