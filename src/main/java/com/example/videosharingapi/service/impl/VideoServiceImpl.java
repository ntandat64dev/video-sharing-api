package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.mapper.VideoMapper;
import com.example.videosharingapi.mapper.VideoRatingMapper;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.repository.HashtagRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoRatingRepository;
import com.example.videosharingapi.repository.VideoRepository;
import com.example.videosharingapi.service.StorageService;
import com.example.videosharingapi.service.VideoService;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final HashtagRepository hashtagRepository;

    private final StorageService storageService;

    private final VideoMapper videoMapper;
    private final VideoRatingMapper videoRatingMapper;

    public VideoServiceImpl(
            VideoRepository videoRepository, UserRepository userRepository, VideoMapper videoMapper,
            VideoRatingRepository videoRatingRepository, StorageService storageService,
            HashtagRepository hashtagRepository, VideoRatingMapper videoRatingMapper
    ) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.videoRatingRepository = videoRatingRepository;
        this.videoMapper = videoMapper;
        this.storageService = storageService;
        this.hashtagRepository = hashtagRepository;
        this.videoRatingMapper = videoRatingMapper;
    }

    @Override
    public VideoDto getVideoById(UUID id) {
        return videoRepository
                .findById(id)
                .map(videoMapper::toVideoDto)
                .orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getVideosByAllCategories(UUID userId) {
        // TODO: Get actual recommend videos
        return videoRepository.findAll().stream()
                .filter(video -> !video.getUser().getId().equals(userId))
                .map(videoMapper::toVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getRelatedVideos(UUID videoId, UUID userId) {
        // TODO: Get actual related videos
        return videoRepository.findAll().stream()
                .filter(video -> !video.getUser().getId().equals(userId))
                .map(videoMapper::toVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto) {
        storageService.store(videoFile, videoDto);
        var video = videoRepository.save(videoMapper.toVideo(videoDto));
        saveHashtags(videoDto.getSnippet().getHashtags(), video);
        videoDto.setId(video.getId());
        return videoDto;
    }

    private void saveHashtags(@Nullable List<String> tags, Video video) {
        if (tags == null) return;
        var hashtags = tags.stream()
                .map(Hashtag::new)
                .map(hashtagRepository::saveIfAbsent)
                .toList();
        video.setHashtags(hashtags);
    }

    @Override
    public void rateVideo(UUID videoId, UUID userId, String rating) {
        var video = videoRepository.getReferenceById(videoId);
        var user = userRepository.getReferenceById(userId);

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), video.getId());
        if (videoRating == null && Objects.equals(rating, VideoRatingDto.NONE)) return;
        if (videoRating != null && videoRating.getRating().name().equalsIgnoreCase(rating)) return;

        if (videoRating != null) {
            if (Objects.equals(rating, VideoRatingDto.NONE)) {
                videoRatingRepository.delete(videoRating);
            } else {
                videoRating.setRating(VideoRating.Rating.valueOf(rating.toUpperCase()));
                videoRating.setPublishedAt(LocalDateTime.now());
                videoRatingRepository.save(videoRating);
            }
        } else {
            videoRating = new VideoRating();
            videoRating.setVideo(video);
            videoRating.setUser(user);
            videoRating.setRating(VideoRating.Rating.valueOf(rating.toUpperCase()));
            videoRating.setPublishedAt(LocalDateTime.now());
            videoRatingRepository.save(videoRating);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VideoRatingDto getRating(UUID videoId, UUID userId) {
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        if (videoRating == null) return videoRatingMapper.fromNullVideoRating(videoId, userId);
        return videoRatingMapper.toVideoRatingDto(videoRating);
    }
}