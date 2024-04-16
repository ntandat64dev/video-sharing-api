package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    void store(MultipartFile file, VideoDto videoDto);
}
