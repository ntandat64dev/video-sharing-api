package com.example.videosharingapi.testutil;

import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.repository.*;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

public class InsertDataExtension implements BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        cleanUpDatabase(context);

        var userRepository = SpringExtension.getApplicationContext(context).getBean(UserRepository.class);
        var videoRepository = SpringExtension.getApplicationContext(context).getBean(VideoRepository.class);
        var videoHashtagRepository = SpringExtension.getApplicationContext(context).getBean(VideoHashtagRepository.class);
        var hashtagRepository = SpringExtension.getApplicationContext(context).getBean(HashtagRepository.class);
        var channelRepository = SpringExtension.getApplicationContext(context).getBean(ChannelRepository.class);
        var visibilityRepository = SpringExtension.getApplicationContext(context).getBean(VisibilityRepository.class);

        var user = User.builder()
                .email("user@gmail.com")
                .password("00000000")
                .build();
        userRepository.save(user);

        var channel = new Channel();
        channel.setName(user.getEmail());
        channel.setJoinDate(LocalDateTime.now());
        channel.setPictureUrl("/default_avatar.png");
        channel.setUser(user);
        channelRepository.save(channel);

        var visibilityPrivate = new Visibility();
        visibilityPrivate.setLevel(Visibility.VisibilityLevel.PRIVATE);
        visibilityRepository.save(visibilityPrivate);
        var visibilityPublic = new Visibility();
        visibilityPublic.setLevel(Visibility.VisibilityLevel.PUBLIC);
        visibilityRepository.save(visibilityPublic);

        var video1 = Video.builder()
                .title("Video 1")
                .description("Video 1 description")
                .thumbnailUrl("Video 1 thumbnail URL")
                .videoUrl("Video 1 video URL")
                .durationSec(1000)
                .uploadDate(LocalDateTime.parse("2024-04-01T09:00:00"))
                .visibility(visibilityPrivate)
                .isCommentAllowed(true)
                .isMadeForKids(false)
                .isAgeRestricted(false)
                .location("United State")
                .user(user)
                .build();
        video1.setVideoSpec(new VideoSpec());
        videoRepository.save(video1);
        var video2 = Video.builder()
                .title("Video 2")
                .description("Video 2 description")
                .thumbnailUrl("Video 2 thumbnail URL")
                .videoUrl("Video 2 video URL")
                .durationSec(2000)
                .uploadDate(LocalDateTime.parse("2024-04-05T09:00:00"))
                .visibility(visibilityPublic)
                .isCommentAllowed(true)
                .isMadeForKids(false)
                .isAgeRestricted(false)
                .location("Vietnam")
                .user(user)
                .build();
        video2.setVideoSpec(new VideoSpec());
        videoRepository.save(video2);
        var hashtag = new Hashtag("music");
        hashtagRepository.save(hashtag);
        var videoHashtag = new VideoHashtag();
        videoHashtag.setVideo(video2);
        videoHashtag.setHashtag(hashtag);
        videoHashtagRepository.save(videoHashtag);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        cleanUpDatabase(context);
    }

    protected void cleanUpDatabase(ExtensionContext context) {
        var userRepository = SpringExtension.getApplicationContext(context).getBean(UserRepository.class);
        var videoRepository = SpringExtension.getApplicationContext(context).getBean(VideoRepository.class);
        var hashtagRepository = SpringExtension.getApplicationContext(context).getBean(HashtagRepository.class);
        var videoHashtagRepository = SpringExtension.getApplicationContext(context).getBean(VideoHashtagRepository.class);
        var channelRepository = SpringExtension.getApplicationContext(context).getBean(ChannelRepository.class);
        var visibilityRepository = SpringExtension.getApplicationContext(context).getBean(VisibilityRepository.class);
        var videoSpecRepository = SpringExtension.getApplicationContext(context).getBean(VideoSpecRepository.class);

        videoSpecRepository.deleteAll();
        videoHashtagRepository.deleteAll();
        hashtagRepository.deleteAll();
        videoRepository.deleteAll();
        visibilityRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }
}
