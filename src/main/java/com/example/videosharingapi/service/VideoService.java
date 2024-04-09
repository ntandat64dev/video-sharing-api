package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.ViewResponse;

import java.util.List;
import java.util.UUID;

public interface VideoService {
    List<VideoDto> getAllVideos();

    List<VideoDto> getRecommendVideos(UUID userId);

    VideoDto saveVideo(VideoDto videoDto);

    ViewResponse viewVideo(ViewRequest viewRequest);

    void rateVideo(RatingRequest ratingRequest);

    void comment(UUID videoId, UUID userId, String content);

    void reply(UUID videoId, UUID commentId, UUID userId, String content);

    void rateComment(UUID commentId, UUID userId, boolean isLike);
}
