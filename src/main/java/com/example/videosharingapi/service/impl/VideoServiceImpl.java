package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.payload.UserDto;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.VideoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll()
                .stream().map(video -> new VideoDto(video.getId(), video.getTitle(), video.getDescription(), video.getThumbnailUrl(),
                        video.getVideoUrl(), new UserDto(video.getUser().getId(), video.getUser().getEmail())))
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto save(VideoDto videoDto) {
        var video = Video.builder()
                .title(videoDto.getTitle())
                .description(videoDto.getDescription())
                .thumbnailUrl(videoDto.getThumbnailUrl())
                .videoUrl(videoDto.getVideoUrl())
                .user(User.builder().id(videoDto.getUser().id()).build())
                .build();
        var savedVideo = videoRepository.save(video);
        var userPref = userRepository.getReferenceById(savedVideo.getUser().getId());
        videoDto.setId(savedVideo.getId());
        videoDto.setUser(new UserDto(userPref.getId(), userPref.getEmail()));
        return videoDto;
    }
}
