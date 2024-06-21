package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void storeVideo(MultipartFile file, MultipartFile thumbnailFile, VideoDto videoDto);

    String storeVideoFile(MultipartFile videoFile);

    String storeThumbnailImage(MultipartFile imageFile);
}
