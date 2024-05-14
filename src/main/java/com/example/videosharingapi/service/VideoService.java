package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    VideoDto saveVideo(MultipartFile videoFile, MultipartFile thumbnailFile, VideoDto videoDto);

    VideoDto updateVideo(MultipartFile thumbnailFile, VideoDto videoDto);

    void deleteVideoById(String id);

    List<VideoDto> getAllVideos();

    List<VideoDto> getVideosByUserId(String userId);

    VideoDto getVideoById(String id);

    List<String> getCategoriesForUserId(String userId);

    List<VideoDto> getVideosByCategoryAll(String userId);

    List<VideoDto> getRelatedVideos(String videoId, String userId);

    VideoRatingDto rateVideo(String videoId, String userId, String rating);

    VideoRatingDto getRating(String videoId, String userId);
}
