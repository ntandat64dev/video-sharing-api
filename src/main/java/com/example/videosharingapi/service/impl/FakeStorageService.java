package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.service.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@Profile({ "dev", "test" })
public class FakeStorageService implements StorageService {

    @Override
    public VideoDto store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApplicationException(HttpStatus.valueOf(400), "Video file is empty!");
        }
        var duration = (int) file.getSize();
        var thumbnailUrl = "/sample_video_thumbnail.jpg";
        var videoUrl = "/SampleVideo_1280x720_10mb.mp4";

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl(thumbnailUrl);
        thumbnail.setWidth(100);
        thumbnail.setHeight(100);

        return VideoDto.builder()
                .thumbnails(List.of(thumbnail))
                .videoUrl(videoUrl)
                .durationSec(duration)
                .build();
    }
}
