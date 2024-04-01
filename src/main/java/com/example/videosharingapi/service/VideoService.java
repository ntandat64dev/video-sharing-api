package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;

import java.util.List;
import java.util.UUID;

public interface VideoService {
    List<VideoDto> getAllVideos();

    List<VideoDto> getRecommendVideos(UUID userId);

    VideoDto save(VideoDto videoDto);
}
