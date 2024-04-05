package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.VideoDto;

import java.util.List;
import java.util.UUID;

public interface VideoService {
    List<VideoDto> getAllVideos();

    List<VideoDto> getRecommendVideos(UUID userId);

    VideoDto save(VideoDto videoDto);

    void viewVideo(UUID videoId, UUID userId);

    void likeVideo(UUID videoId, UUID userId, boolean isLike);

    void comment(UUID videoId, UUID userId, String content);

    void reply(UUID videoId, UUID commentId, UUID userId, String content);

    void likeComment(UUID commentId, UUID userId, boolean isLike);
}
