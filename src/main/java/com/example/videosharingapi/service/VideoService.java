package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VideoService {

    VideoDto getVideoById(UUID id);

    List<VideoDto> getVideosByAllCategories(UUID userId);

    List<VideoDto> getRelatedVideos(UUID videoId, UUID userId);

    VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto);

    void rateVideo(UUID videoId, UUID userId, String rating);

    VideoRatingDto getRating(UUID videoId, UUID userId);
}
