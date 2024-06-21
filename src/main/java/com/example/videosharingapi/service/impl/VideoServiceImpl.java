package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.*;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.VideoMapper;
import com.example.videosharingapi.mapper.VideoRatingMapper;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.NotificationService;
import com.example.videosharingapi.service.StorageService;
import com.example.videosharingapi.service.UserService;
import com.example.videosharingapi.service.VideoService;
import com.example.videosharingapi.validation.group.Save;
import jakarta.validation.Validator;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final VideoRatingRepository videoRatingRepository;
    private final HashtagRepository hashtagRepository;
    private final ViewHistoryRepository viewHistoryRepository;
    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final PlaylistItemRepository playlistItemRepository;

    private final UserService userService;
    private final StorageService storageService;
    private final NotificationService notificationService;

    private final VideoMapper videoMapper;
    private final VideoRatingMapper videoRatingMapper;
    private final Validator validator;

    @Override
    @Transactional
    public VideoDto saveVideo(MultipartFile videoFile, MultipartFile thumbnailFile, VideoDto videoDto) {
        if (!userService.getAuthenticatedUser().getUserId().equals(videoDto.getSnippet().getUserId())) {
            // If uploader is not authenticated user ID.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        storageService.storeVideo(videoFile, thumbnailFile, videoDto);

        var constraintViolations = validator.validate(videoDto, Default.class, Save.class);
        if (!constraintViolations.isEmpty()) throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);

        var video = videoRepository.save(videoMapper.toVideo(videoDto));
        saveHashtags(videoDto.getSnippet().getHashtags(), video);

        // Create notification.
        var notification = new NotificationDto();
        notification.setSnippet(NotificationDto.Snippet.builder()
                .actionType(1)
                .actorId(video.getUser().getId())
                .objectType(NotificationObject.ObjectType.VIDEO)
                .objectId(video.getId())
                .build());
        notificationService.createNotification(notification);

        log.info("Uploaded video: videoId={}", video.getId());

        return videoMapper.toVideoDto(video);
    }

    private void saveHashtags(List<String> tags, Video video) {
        if (tags == null) return;
        var hashtags = tags.stream()
                .map(Hashtag::new)
                .map(hashtagRepository::saveIfAbsent)
                .collect(Collectors.toList());
        video.setHashtags(hashtags);
    }

    @Override
    @Transactional
    public VideoDto updateVideo(MultipartFile thumbnailFile, VideoDto videoDto) {
        var videoOwner = userRepository.findByVideoId(videoDto.getId());
        var user = userService.getAuthenticatedUser();

        // If the video owner is not the authorized user, then throw forbidden error.
        if (!Objects.equals(videoOwner.getId(), user.getUserId())) throw new AppException(ErrorCode.FORBIDDEN);

        // Set null to values that user does not permission to update.
        // The mapper will skip these values.
        videoDto.setStatistic(null);
        videoDto.getSnippet().setDuration(null);
        videoDto.getSnippet().setVideoUrl(null);
        videoDto.getSnippet().setPublishedAt(null);
        videoDto.getSnippet().setThumbnails(null);

        // Update video.
        var video = videoRepository.findById(videoDto.getId()).orElseThrow();
        videoMapper.updateVideo(video, videoDto);

        // Update hashtags.
        saveHashtags(videoDto.getSnippet().getHashtags(), video);

        // Trigger Video document update
        videoRepository.save(video);

        log.info("Updated video: videoId={}", video.getId());

        return videoMapper.toVideoDto(video);
    }

    @Override
    @Transactional
    public void deleteVideoById(String id) {
        var user = userService.getAuthenticatedUser();
        var video = videoRepository.findById(id).orElseThrow();
        if (!video.getUser().getId().equals(user.getUserId())) throw new AppException(ErrorCode.FORBIDDEN);

        deleteMetadata(id);
    }

    private void deleteMetadata(String videoId) {
        // Delete ViewHistory.
        viewHistoryRepository.deleteByVideoId(videoId);

        // Delete Comment.
        commentRatingRepository.deleteByCommentVideoId(videoId);
        commentRepository.deleteByVideoId(videoId);

        // Delete PlaylistItem.
        playlistItemRepository.deleteAllByVideoId(videoId);

        // Delete VideoRating.
        videoRatingRepository.deleteByVideoId(videoId);

        // Delete Video.
        videoRepository.deleteById(videoId);

        // Delete related notifications.
        notificationService.deleteRelatedNotifications(videoId);
    }

    @Override
    @Transactional
    public VideoDto changeThumbnail(MultipartFile imageFile, String videoId, String userId) {
        var video = videoRepository.findById(videoId).orElseThrow();

        // If the userId is not the one who created the video, then throw forbidden error.
        if (!Objects.equals(userId, video.getUser().getId())) throw new AppException(ErrorCode.FORBIDDEN);

        var thumbnailUrl = storageService.storeThumbnailImage(imageFile);
        if (thumbnailUrl == null) throw new AppException(ErrorCode.SOMETHING_WENT_WRONG);

        var thumbnail = new Thumbnail();
        thumbnail.setType(Thumbnail.Type.DEFAULT);
        thumbnail.setUrl(thumbnailUrl);
        thumbnail.setWidth(100);
        thumbnail.setHeight(100);

        video.getThumbnails().clear();
        video.getThumbnails().add(thumbnail);

        log.info("Profile image changed: videoId={}", videoId);

        return videoMapper.toVideoDto(video);
    }

    @Override
    public PageResponse<VideoDto> getAllVideos(Pageable pageable) {
        var videoDtoPage = videoRepository.findAll(pageable).map(videoMapper::toVideoDto);
        return new PageResponse<>(videoDtoPage);
    }

    @Override
    public PageResponse<VideoDto> getVideosByUserId(String userId, Pageable pageable) {
        var videoDtoPage = videoRepository.findAllByUserId(userId, pageable).map(videoMapper::toVideoDto);
        return new PageResponse<>(videoDtoPage);
    }

    @Override
    public VideoDto getVideoById(String id) {
        return videoRepository
                .findById(id)
                .map(videoMapper::toVideoDto)
                .orElseThrow();
    }

    @Override
    public List<String> getCategoriesForUserId(String userId) {
        // TODO: Implement and test
        return hashtagRepository.findAll(PageRequest.of(0, 10)).stream()
                .map(Hashtag::getTag)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<VideoDto> getVideosByCategoryAll(String userId, Pageable pageable) {
        // TODO: Implement
        var videoDtoList = videoRepository.findAll().stream()
                .filter(video -> !video.getUser().getId().equals(userId))
                .map(videoMapper::toVideoDto)
                .toList();

        final int start = pageable.isPaged()
                ? (int) Math.min(pageable.getOffset(), videoDtoList.size())
                : 0;
        final int end = pageable.isPaged()
                ? Math.min((start + pageable.getPageSize()), videoDtoList.size())
                : videoDtoList.size();
        final var page = new PageImpl<>(videoDtoList.subList(start, end), pageable, videoDtoList.size());

        return new PageResponse<>(page);
    }

    @Override
    public PageResponse<VideoDto> getRelatedVideos(String videoId, String userId, Pageable pageable) {
        // TODO: Implement
        var videoDtoList = videoRepository.findAll().stream()
                .filter(video -> !video.getUser().getId().equals(userId))
                .map(videoMapper::toVideoDto)
                .toList();

        final int start = pageable.isPaged()
                ? (int) Math.min(pageable.getOffset(), videoDtoList.size())
                : 0;
        final int end = pageable.isPaged()
                ? Math.min((start + pageable.getPageSize()), videoDtoList.size())
                : videoDtoList.size();
        final var page = new PageImpl<>(videoDtoList.subList(start, end), pageable, videoDtoList.size());

        return new PageResponse<>(page);
    }

    @Override
    @Transactional
    public void viewVideo(String videoId, String userId) {
        var video = videoRepository.findById(videoId).orElseThrow();
        var user = userRepository.findById(userId).orElseThrow();
        ViewHistory viewHistory = new ViewHistory();
        viewHistory.setVideo(video);
        viewHistory.setUser(user);
        viewHistory.setPublishedAt(LocalDateTime.now());
        viewHistory.setViewedDurationSec(0);
        viewHistoryRepository.save(viewHistory);
    }

    @Override
    @Transactional
    public VideoRatingDto rateVideo(String videoId, String userId, String rating) {
        var video = videoRepository.findById(videoId).orElseThrow();
        var user = userRepository.findById(userId).orElseThrow();

        var videoRating = videoRatingRepository.findByUserIdAndVideoId(user.getId(), video.getId());

        if (videoRating == null && Objects.equals(rating, VideoRatingDto.NONE)) {
            // If the user rates NONE but there is no rating yet, return NONE rating.
            return videoRatingMapper.createNoneVideoRating(videoId, userId);
        }

        if (videoRating != null && videoRating.getRating().name().equalsIgnoreCase(rating)) {
            // If the user rates the same rating, do nothing and return.
            return videoRatingMapper.toVideoRatingDto(videoRating);
        }

        if (videoRating != null) {
            if (Objects.equals(rating, VideoRatingDto.NONE)) {
                // If the user rates NONE, then delete the rating and return NONE.
                videoRatingRepository.delete(videoRating);
                return videoRatingMapper.createNoneVideoRating(videoId, userId);
            } else {
                // else update the current rating.
                videoRating.setRating(VideoRating.Rating.valueOf(rating.toUpperCase()));
                videoRating.setPublishedAt(LocalDateTime.now());
                videoRatingRepository.save(videoRating);
            }
        } else {
            // If this is the first time the user has rated the video,
            // then create a new rating and save.
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
    public VideoRatingDto getRating(String videoId, String userId) {
        var videoRating = videoRatingRepository.findByUserIdAndVideoId(userId, videoId);
        if (videoRating == null) return videoRatingMapper.createNoneVideoRating(videoId, userId);
        return videoRatingMapper.toVideoRatingDto(videoRating);
    }

    @Override
    public PageResponse<VideoDto> getFollowingVideos(String userId, Pageable pageable) {
        var videoDtoPage = videoRepository.findFollowingVideos(userId, pageable).map(videoMapper::toVideoDto);
        return new PageResponse<>(videoDtoPage);
    }
}