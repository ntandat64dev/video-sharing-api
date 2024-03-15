package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.service.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import video.api.client.ApiVideoClient;
import video.api.client.api.ApiException;
import video.api.client.api.models.VideoCreationPayload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Service
public class StorageServiceImpl implements StorageService {

    @Override
    public VideoDto store(MultipartFile file) {
        final var client = new ApiVideoClient("TR8of6JJu0OlDbTS6Rwp9PT3M8hTXPl4PQH6GdoPysI");
        try {
            var videoFile = new File("/home/dell/IdeaProjects/video-sharing-api/new_video.mp4");
            file.transferTo(videoFile);
            var video = client.videos().create(new VideoCreationPayload());
            video = client.videos().upload(video.getVideoId(), videoFile);
            Files.delete(videoFile.toPath());
            return VideoDto.builder()
                    .thumbnailUrl(Objects.requireNonNull(Objects.requireNonNull(video.getAssets()).getThumbnail()).toURL().toString())
                    .videoUrl(Objects.requireNonNull(video.getAssets().getMp4()).toString())
                    .build();
        } catch (ApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
