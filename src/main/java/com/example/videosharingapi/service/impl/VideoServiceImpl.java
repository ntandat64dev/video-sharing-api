package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.validation.group.Save;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.exception.ApplicationException;
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
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
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
    private final Validator validator;

    public VideoServiceImpl(
            VideoRepository videoRepository, UserRepository userRepository, VideoMapper videoMapper,
            VideoRatingRepository videoRatingRepository, StorageService storageService,
            HashtagRepository hashtagRepository, VideoRatingMapper videoRatingMapper, Validator validator
    ) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.videoRatingRepository = videoRatingRepository;
        this.videoMapper = videoMapper;
        this.storageService = storageService;
        this.hashtagRepository = hashtagRepository;
        this.videoRatingMapper = videoRatingMapper;
        this.validator = validator;
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

        var constraintViolations = validator.validate(videoDto, Default.class, Save.class);
        if (!constraintViolations.isEmpty()) {
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong");
        }

        var video = videoRepository.save(videoMapper.toVideo(videoDto));
        saveHashtags(videoDto.getSnippet().getHashtags(), video);

        return videoRepository.findById(video.getId())
                .map(videoMapper::toVideoDto)
                .orElseThrow();
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
    public VideoRatingDto rateVideo(UUID videoId, UUID userId, String rating) {
        var video = videoRepository.getReferenceById(videoId);
        var user = userRepository.getReferenceById(userId);

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), video.getId());
        if (videoRating == null && Objects.equals(rating, VideoRatingDto.NONE)) {
            return videoRatingMapper.fromNullVideoRating(videoId, userId);
        }
        if (videoRating != null && videoRating.getRating().name().equalsIgnoreCase(rating)) {
            return videoRatingMapper.toVideoRatingDto(videoRating);
        }

        if (videoRating != null) {
            if (Objects.equals(rating, VideoRatingDto.NONE)) {
                videoRatingRepository.delete(videoRating);
                return videoRatingMapper.fromNullVideoRating(videoId, userId);
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
        return videoRatingMapper.toVideoRatingDto(videoRating);
    }

    @Override
    @Transactional(readOnly = true)
    public VideoRatingDto getRating(UUID videoId, UUID userId) {
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        if (videoRating == null) return videoRatingMapper.fromNullVideoRating(videoId, userId);
        return videoRatingMapper.toVideoRatingDto(videoRating);
    }
}