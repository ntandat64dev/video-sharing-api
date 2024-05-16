package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {

    VideoDto saveVideo(MultipartFile videoFile, MultipartFile thumbnailFile, VideoDto videoDto);

    VideoDto updateVideo(MultipartFile thumbnailFile, VideoDto videoDto);

    void deleteVideoById(String id);

    PageResponse<VideoDto> getAllVideos(Pageable pageable);

    PageResponse<VideoDto> getVideosByUserId(String userId, Pageable pageable);

    VideoDto getVideoById(String id);

    List<String> getCategoriesForUserId(String userId);

    PageResponse<VideoDto> getVideosByCategoryAll(String userId, Pageable pageable);

    PageResponse<VideoDto> getRelatedVideos(String videoId, String userId, Pageable pageable);

    VideoRatingDto rateVideo(String videoId, String userId, String rating);

    VideoRatingDto getRating(String videoId, String userId);

    PageResponse<VideoDto> getFollowingVideos(String userId, Pageable pageable);
}
