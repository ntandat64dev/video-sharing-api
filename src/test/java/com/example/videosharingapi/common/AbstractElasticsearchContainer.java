package com.example.videosharingapi.common;

import com.example.videosharingapi.document.Playlist;
import com.example.videosharingapi.document.User;
import com.example.videosharingapi.document.Video;
import com.example.videosharingapi.elasticsearchrepository.PlaylistElasticsearchRepository;
import com.example.videosharingapi.elasticsearchrepository.UserElasticsearchRepository;
import com.example.videosharingapi.elasticsearchrepository.VideoElasticsearchRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import java.time.LocalDateTime;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractElasticsearchContainer {
    private static final String ELASTICSEARCH_IMAGE = "elasticsearch:8.13.4";

    private @Autowired VideoElasticsearchRepository videoElasticsearchRepository;
    private @Autowired UserElasticsearchRepository userElasticsearchRepository;
    private @Autowired PlaylistElasticsearchRepository playlistElasticsearchRepository;

    protected static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false");

    static {
        elasticsearch.start();
    }

    @DynamicPropertySource
    private static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }

    @BeforeAll
    public void beforeAll() {
        prepareData();
    }

    @AfterAll
    public void afterAll() {
        clearData();
    }

    protected void prepareData() {
        clearData();

        var video1 = new Video();
        video1.setId("37b32dc2");
        video1.setTitle("Video 1");
        video1.setDescription("Video 1 description");
        video1.setPublishedDate(LocalDateTime.parse("2024-04-01T09:00:00"));
        video1.setViewCount(4L);

        var video2 = new Video();
        video2.setId("f7d9b74b");
        video2.setTitle("Video 2");
        video2.setDescription("Video 2 description");
        video2.setPublishedDate(LocalDateTime.parse("2024-04-02T09:00:00"));
        video2.setViewCount(3L);

        var video3 = new Video();
        video3.setId("e65707b4");
        video3.setTitle("Video 3");
        video3.setDescription("Video 3 description");
        video3.setPublishedDate(LocalDateTime.parse("2024-04-03T09:00:00"));
        video3.setViewCount(2L);

        videoElasticsearchRepository.saveAll(List.of(video1, video2, video3));

        var admin = new User();
        admin.setId("3f06af63");
        admin.setUsername("admin");
        admin.setBio(null);

        var user1 = new User();
        user1.setId("a05990b1");
        user1.setUsername("user1");
        user1.setBio(null);

        var user2 = new User();
        user2.setId("9b79f4ba");
        user2.setUsername("user2");
        user2.setBio(null);

        var user3 = new User();
        user3.setId("d540fce2");
        user3.setUsername("user3");
        user3.setBio(null);

        userElasticsearchRepository.saveAll(List.of(admin, user1, user2, user3));

        var playlist1 = new Playlist();
        playlist1.setId("d8659362");
        playlist1.setTitle("My Videos");
        playlist1.setDescription(null);
        playlist1.setVisible(true);

        var playlist2 = new Playlist();
        playlist2.setId("bae06c8a");
        playlist2.setTitle("Watch Later");
        playlist2.setDescription(null);
        playlist2.setVisible(false);

        var playlist3 = new Playlist();
        playlist3.setId("c31760ea");
        playlist3.setTitle("Liked Videos");
        playlist3.setDescription(null);
        playlist3.setVisible(false);

        var playlist4 = new Playlist();
        playlist4.setId("236e2aa6");
        playlist4.setTitle("Watch Later");
        playlist4.setDescription(null);
        playlist4.setVisible(false);

        var playlist5 = new Playlist();
        playlist5.setId("d07f1bee");
        playlist5.setTitle("Liked Videos");
        playlist5.setDescription(null);
        playlist5.setVisible(false);

        playlistElasticsearchRepository.saveAll(List.of(playlist1, playlist2, playlist3, playlist4, playlist5));
    }

    protected void clearData() {
        videoElasticsearchRepository.deleteAll();
        userElasticsearchRepository.deleteAll();
        playlistElasticsearchRepository.deleteAll();
    }
}
