package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface StorageService {
    VideoDto store(MultipartFile file);
}
