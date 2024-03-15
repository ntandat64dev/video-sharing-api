package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface VideoService {
    List<VideoDto> getAllVideos();

    VideoDto save(VideoDto videoDto);
}
