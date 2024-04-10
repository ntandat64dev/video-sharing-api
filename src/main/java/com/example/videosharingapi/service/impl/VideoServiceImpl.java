package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.mapper.VideoVideoDtoMapper;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.RatingResponse;
import com.example.videosharingapi.payload.response.ViewResponse;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.VideoService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;
    private final VideoHashtagRepository videoHashtagRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final VideoRatingRepository videoRatingRepository;

    private final MessageSource messageSource;
    private final VideoVideoDtoMapper videoVideoDtoMapper;
    private final VideoSpecRepository videoSpecRepository;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository, HashtagRepository hashtagRepository,
                            VideoHashtagRepository videoHashtagRepository, ViewHistoryRepository viewHistoryRepository,
                            VideoRatingRepository videoRatingRepository,
                            MessageSource messageSource, VideoVideoDtoMapper videoVideoDtoMapper, VideoSpecRepository videoSpecRepository) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.hashtagRepository = hashtagRepository;
        this.videoHashtagRepository = videoHashtagRepository;
        this.viewHistoryRepository = viewHistoryRepository;
        this.videoRatingRepository = videoRatingRepository;
        this.messageSource = messageSource;
        this.videoVideoDtoMapper = videoVideoDtoMapper;
        this.videoSpecRepository = videoSpecRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public VideoDto getVideoById(UUID videoId) {
        return videoRepository.findById(videoId)
                .map(videoVideoDtoMapper::videoToVideoDto)
                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, messageSource.getMessage("exception.video.id.not-exist",
                        new Object[] { videoId }, LocaleContextHolder.getLocale())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getRecommendVideos(UUID userId) {
        // TODO: Get actual recommend videos
        return videoRepository.findAllByUserId(userId).stream()
                .map(videoVideoDtoMapper::videoToVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getRelatedVideos(UUID videoId, UUID userId) {
        // TODO: Get actual related videos
        return videoRepository.findAllByUserId(userId).stream()
                .map(videoVideoDtoMapper::videoToVideoDto)
                .collect(Collectors.toList());
    }

    @Override
    public VideoDto saveVideo(VideoDto videoDto) {
        if (!userRepository.existsById(videoDto.getUserId()))
            throw new ApplicationException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("exception.user.id.not-exist",
                            new Object[] { videoDto.getUserId() }, LocaleContextHolder.getLocale()));

        var video = videoRepository.save(videoVideoDtoMapper.videoDtoToVideo(videoDto));
        saveHashtag(videoDto.getHashtags(), video);
        videoDto.setId(video.getId());
        videoDto.setUploadDate(video.getUploadDate());
        return videoDto;
    }

    private void saveHashtag(Set<String> hashtags, Video video) {
        if (hashtags != null) {
            var videoTags = new ArrayList<VideoHashtag>();
            hashtags.forEach(tagString -> {
                var hashtag = new Hashtag();
                hashtag.setTag(tagString);
                var savedHashtag = hashtagRepository.saveIfNotExist(hashtag);
                var videoHashtag = new VideoHashtag();
                videoHashtag.setHashtag(savedHashtag);
                videoHashtag.setVideo(video);
                videoTags.add(videoHashtag);
            });
            videoHashtagRepository.saveAll(videoTags);
        }
    }

    @Override
    public ViewResponse viewVideo(ViewRequest viewRequest) {
        // TODO: Test this function
        checkUserIdAndVideoIdExistent(viewRequest.getUserId(), viewRequest.getVideoId());

        var video = videoRepository.getReferenceById(viewRequest.getVideoId());
        var user = userRepository.getReferenceById(viewRequest.getUserId());

        var lastViewHistory = viewHistoryRepository.findLatest(viewRequest.getVideoId(), viewRequest.getUserId());
        if (lastViewHistory != null) {
            if (ChronoUnit.SECONDS.between(lastViewHistory.getViewedAt(), viewRequest.getViewedAt()) > video.getDurationSec() &&
                    viewRequest.getDuration() >= 30) {
                // If this view's duration greater than the last by video duration and video duration >= 30.
                var viewHistory = new ViewHistory();
                viewHistory.setVideo(video);
                viewHistory.setUser(user);
                viewHistory.setViewedAt(viewRequest.getViewedAt());
                viewHistory.setViewedDuration(viewRequest.getDuration());
                viewHistoryRepository.save(viewHistory);
            }
        } else {
            var viewHistory = new ViewHistory();
            viewHistory.setVideo(video);
            viewHistory.setUser(user);
            viewHistory.setViewedAt(viewRequest.getViewedAt());
            viewHistory.setViewedDuration(viewRequest.getDuration());
            viewHistoryRepository.save(viewHistory);
        }

        var viewCount = viewHistoryRepository.countByVideoId(viewRequest.getVideoId());

        return ViewResponse.builder()
                .videoId(viewRequest.getVideoId())
                .userId(viewRequest.getUserId())
                .haveViewedBefore(viewCount > 1)
                .viewCount(viewCount)
                .build();
    }

    @Override
    public void rateVideo(RatingRequest ratingRequest) {
        checkUserIdAndVideoIdExistent(ratingRequest.getUserId(), ratingRequest.getVideoId());

        var video = videoRepository.getReferenceById(ratingRequest.getVideoId());
        var user = userRepository.getReferenceById(ratingRequest.getUserId());

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), video.getId());
        if (videoRating == null && ratingRequest.getRating() == RatingRequest.RatingType.NONE) return;
        if (videoRating != null && videoRating.getRating().name().equals(ratingRequest.getRating().name())) return;

        var videoSpec = videoSpecRepository.findById(video.getId());
        if (videoSpec.isEmpty())
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessage("message.some-thing-went-wrong", null, LocaleContextHolder.getLocale()));

        if (videoRating != null) {
            if (ratingRequest.getRating() == RatingRequest.RatingType.NONE) {
                videoRatingRepository.delete(videoRating);
            } else {
                videoRating.setRating(VideoRating.Rating.valueOf(ratingRequest.getRating().name()));
                videoRating.setRatedAt(ratingRequest.getRatedAt());
                videoRatingRepository.save(videoRating);
            }
        } else {
            videoRating = new VideoRating();
            videoRating.setVideo(video);
            videoRating.setUser(user);
            videoRating.setRating(VideoRating.Rating.valueOf(ratingRequest.getRating().name()));
            videoRating.setRatedAt(ratingRequest.getRatedAt());
            videoRatingRepository.save(videoRating);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse getRating(UUID videoId, UUID userId) {
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        var ratingResponse = new RatingResponse();
        ratingResponse.setVideoId(videoId);
        ratingResponse.setRating(videoRating == null
                ? RatingResponse.RatingType.NONE
                : RatingResponse.RatingType.valueOf(videoRating.getRating().name()));
        ratingResponse.setRatedBy(userId);
        ratingResponse.setRatedAt(videoRating != null ? videoRating.getRatedAt() : null);
        return ratingResponse;
    }

    private void checkUserIdAndVideoIdExistent(UUID userId, UUID videoId) throws ApplicationException {
        if (!userRepository.existsById(userId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.user.id.not-exist",
                        new Object[] { userId }, LocaleContextHolder.getLocale()));
        if (!videoRepository.existsById(videoId)) throw new ApplicationException(HttpStatus.BAD_REQUEST,
                messageSource.getMessage("exception.video.id.not-exist",
                        new Object[] { videoId }, LocaleContextHolder.getLocale()));
    }
}