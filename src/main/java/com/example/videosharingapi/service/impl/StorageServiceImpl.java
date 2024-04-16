package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.mapper.ThumbnailMapper;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import video.api.client.ApiVideoClient;
import video.api.client.api.ApiException;
import video.api.client.api.models.VideoCreationPayload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Profile("prod")
public class StorageServiceImpl implements StorageService {

    private final ThumbnailMapper thumbnailMapper;

    public StorageServiceImpl(ThumbnailMapper thumbnailMapper) {
        this.thumbnailMapper = thumbnailMapper;
    }

    @Override
    public void store(MultipartFile file, VideoDto videoDto) {
        final var client = new ApiVideoClient("TR8of6JJu0OlDbTS6Rwp9PT3M8hTXPl4PQH6GdoPysI");
        try {
            var videoFile = new File("/home/dell/IdeaProjects/video-sharing-api/new_video.mp4");
            file.transferTo(videoFile);
            var video = client.videos().create(new VideoCreationPayload().title(videoFile.getName()));
            video = client.videos().upload(video.getVideoId(), videoFile);
            Files.delete(videoFile.toPath());

            var thumbnail = new Thumbnail();
            thumbnail.setType(Thumbnail.Type.DEFAULT);
            thumbnail.setUrl(Objects.requireNonNull(Objects.requireNonNull(video.getAssets())
                    .getThumbnail()).toString());
            thumbnail.setWidth(100);
            thumbnail.setHeight(100);

            videoDto.getSnippet().setThumbnails(thumbnailMapper.toMap(List.of(thumbnail)));
            videoDto.getSnippet().setVideoUrl(Objects.requireNonNull(video.getAssets().getMp4()).toString());
            videoDto.getSnippet().setDuration(Duration.ofSeconds(1000));
        } catch (ApiException | IOException e) {
            if (e instanceof ApiException ex)
                throw new ApplicationException(HttpStatus.valueOf(ex.getCode()), ex.getMessage());
            throw new RuntimeException(e);
        }
    }
}
