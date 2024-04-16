package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.RatingResponse;
import com.example.videosharingapi.payload.response.ViewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface VideoService {
    VideoDto getVideoById(UUID videoId);

    List<VideoDto> getRecommendVideos(UUID userId);

    List<VideoDto> getRelatedVideos(UUID videoId, UUID userId);

    VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto);

    ViewResponse viewVideo(ViewRequest viewRequest);

    void rateVideo(RatingRequest ratingRequest);

    RatingResponse getRating(UUID videoId, UUID userId);
}
