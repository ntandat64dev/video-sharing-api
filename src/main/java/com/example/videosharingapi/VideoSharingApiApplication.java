package com.example.videosharingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VideoSharingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoSharingApiApplication.class, args);
    }
}
