package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.mapper.ThumbnailMapper;
import com.example.videosharingapi.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@Profile({ "dev", "test" })
@RequiredArgsConstructor
public class FakeStorageService implements StorageService {

    private final ThumbnailMapper thumbnailMapper;

    @Override
    public void storeVideo(MultipartFile file, MultipartFile thumbnailFile, VideoDto videoDto) {
        videoDto.getSnippet()
                .setVideoUrl("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl("https://dummyimage.com/720x450/ff6b81/fff");
        thumbnail.setWidth(720);
        thumbnail.setHeight(450);
        videoDto.getSnippet().setThumbnails(thumbnailMapper.toMap(List.of(thumbnail)));
    }

    @Override
    public String storeVideoFile(MultipartFile videoFile) {
        return "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
    }

    @Override
    public String storeThumbnailImage(MultipartFile imageFile) {
        return "https://dummyimage.com/720x450/ff6b81/fff";
    }
}
