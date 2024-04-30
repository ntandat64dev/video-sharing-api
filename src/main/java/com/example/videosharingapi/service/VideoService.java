package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    VideoDto getVideoById(String id);

    List<VideoDto> getVideosByAllCategories(String userId);

    List<VideoDto> getRelatedVideos(String videoId, String userId);

    VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto);

    VideoRatingDto rateVideo(String videoId, String userId, String rating);

    VideoRatingDto getRating(String videoId, String userId);
}
