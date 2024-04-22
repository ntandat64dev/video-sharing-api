package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.mapper.VideoMapper;
import com.example.videosharingapi.mapper.VideoRatingMapper;
import com.example.videosharingapi.model.entity.Hashtag;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.model.entity.VideoRating;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.StorageService;
import com.example.videosharingapi.service.VideoService;
import jakarta.annotation.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    private final MessageSource messageSource;
    private final VideoMapper videoMapper;
    private final VideoRatingMapper videoRatingMapper;

    public VideoServiceImpl(
            VideoRepository videoRepository, UserRepository userRepository, VideoMapper videoMapper,
            VideoRatingRepository videoRatingRepository, MessageSource messageSource,
            StorageService storageService, HashtagRepository hashtagRepository, VideoRatingMapper videoRatingMapper
    ) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.videoRatingRepository = videoRatingRepository;
        this.messageSource = messageSource;
        this.videoMapper = videoMapper;
        this.storageService = storageService;
        this.hashtagRepository = hashtagRepository;
        this.videoRatingMapper = videoRatingMapper;
    }

    @Override
    public VideoDto getVideoById(UUID id) {
        if (!videoRepository.existsById(id)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.video.id.not-exist",
                        null, LocaleContextHolder.getLocale()));

        return videoRepository
                .findById(id)
                .map(videoMapper::toVideoDto)
                .orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getVideosByAllCategories(UUID userId) {
        // TODO: Get actual recommend videos
        return videoRepository.findAllByUserId(userId).stream()
                .map(videoMapper::toVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getRelatedVideos(UUID videoId, UUID userId) {
        // TODO: Get actual related videos
        return videoRepository.findAllByUserId(userId).stream()
                .map(videoMapper::toVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto saveVideo(MultipartFile videoFile, VideoDto videoDto) {
        if (!userRepository.existsById(videoDto.getSnippet().getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[]{videoDto.getSnippet().getUserId()}, LocaleContextHolder.getLocale()));

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
        checkUserIdAndVideoIdExistent(userId, videoId);

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
        checkUserIdAndVideoIdExistent(userId, videoId);
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        if (videoRating == null) return videoRatingMapper.fromNullVideoRating(videoId, userId);
        return videoRatingMapper.toVideoRatingDto(videoRating);
    }

    private void checkUserIdAndVideoIdExistent(UUID userId, UUID videoId) throws ApplicationException {
        if (!userRepository.existsById(userId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.user.id.not-exist",
                        new Object[]{userId}, LocaleContextHolder.getLocale()));
        if (!videoRepository.existsById(videoId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.video.id.not-exist",
                        new Object[]{videoId}, LocaleContextHolder.getLocale()));
    }
}