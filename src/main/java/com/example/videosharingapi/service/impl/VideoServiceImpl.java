package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.payload.UserDto;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.VideoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;

    public VideoServiceImpl(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @Override
    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll()
                .stream().map(video -> new VideoDto(video.getId(), video.getTitle(), video.getDescription(), video.getThumbnailUrl(),
                        video.getVideoUrl(), new UserDto(video.getUser().getId(), video.getUser().getEmail(),
                        video.getUser().getPhotoUrl(), video.getUser().getChannelName())))
                .collect(Collectors.toList());
    }
}
