package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;

import java.util.List;

public interface VideoService {
    List<VideoDto> getAllVideos();
}
