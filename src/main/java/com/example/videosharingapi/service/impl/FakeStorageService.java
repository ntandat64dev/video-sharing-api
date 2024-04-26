package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.mapper.ThumbnailMapper;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;

@Service
@Transactional
@Profile({ "dev", "test" })
public class FakeStorageService implements StorageService {

    private final ThumbnailMapper thumbnailMapper;

    public FakeStorageService(ThumbnailMapper thumbnailMapper) {
        this.thumbnailMapper = thumbnailMapper;
    }

    @Override
    public void store(MultipartFile file, VideoDto videoDto) {
        videoDto.getSnippet().setDuration(Duration.ofSeconds(1000));
        videoDto.getSnippet()
                .setVideoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl("https://dummyimage.com/720x450/fff/aaa");
        thumbnail.setWidth(720);
        thumbnail.setHeight(450);
        videoDto.getSnippet().setThumbnails(thumbnailMapper.toMap(List.of(thumbnail)));
    }
}
