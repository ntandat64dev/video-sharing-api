package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    List<VideoDto> getAllVideos();

    List<VideoDto> getVideosByUserId(String userId);

    VideoDto getVideoById(String id);

    VideoDto updateVideo(VideoDto videoDto);

    void deleteVideoById(String id);

    List<String> getCategoriesForUserId(String userId);

    List<VideoDto> getVideosByCategoryAll(String userId);

    List<VideoDto> getRelatedVideos(String videoId, String userId);

    VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto);

    VideoRatingDto rateVideo(String videoId, String userId, String rating);

    VideoRatingDto getRating(String videoId, String userId);
}
