package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.mapper.VideoVideoDtoMapper;
import com.example.videosharingapi.exception.ApplicationException;
import com.example.videosharingapi.model.entity.*;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.repository.*;
import com.example.videosharingapi.service.VideoService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final MessageSource messageSource;
    private final VideoVideoDtoMapper videoVideoDtoMapper;

    public VideoServiceImpl(VideoRepository videoRepository, UserRepository userRepository, HashtagRepository hashtagRepository,
                            VideoHashtagRepository videoHashtagRepository, ViewHistoryRepository viewHistoryRepository,
                            VideoLikeRepository videoLikeRepository, CommentRepository commentRepository,
                            CommentLikeRepository commentLikeRepository, MessageSource messageSource,
                            VideoVideoDtoMapper videoVideoDtoMapper) {
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
        this.hashtagRepository = hashtagRepository;
        this.videoHashtagRepository = videoHashtagRepository;
        this.viewHistoryRepository = viewHistoryRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.messageSource = messageSource;
        this.videoVideoDtoMapper = videoVideoDtoMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<VideoDto> getAllVideos() {
        return videoRepository.findAll().stream()
                .map(videoVideoDtoMapper::videoToVideoDto)
                .collect(Collectors.toList());
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
    public VideoDto save(VideoDto videoDto) {
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
    public void viewVideo(UUID videoId, UUID userId) {
        var video = videoRepository.getReferenceById(videoId);
        var user = userRepository.getReferenceById(userId);
        var viewHistory = new ViewHistory();
        viewHistory.setVideo(video);
        viewHistory.setUser(user);
        viewHistory.setViewedAt(LocalDateTime.now());
        viewHistory.setViewedDuration(30);
        viewHistoryRepository.save(viewHistory);
    }

    @Override
    public void likeVideo(UUID videoId, UUID userId, boolean isLike) {
        var video = videoRepository.getReferenceById(videoId);
        var user = userRepository.getReferenceById(userId);
        var videoLike = new VideoLike();
        videoLike.setVideo(video);
        videoLike.setUser(user);
        videoLike.setLikedAt(LocalDateTime.now());
        videoLike.setIsLike(isLike);
        videoLikeRepository.save(videoLike);
    }

    @Override
    public void comment(UUID videoId, UUID userId, String content) {
        commentVideo(videoId, userId, null, false, content);
    }

    @Override
    public void reply(UUID videoId, UUID commentId, UUID userId, String content) {
        commentVideo(videoId, userId, commentId, true, content);
    }

    private void commentVideo(UUID videoId, UUID userId, UUID commentId, boolean isReply, String content) {
        var video = videoRepository.getReferenceById(videoId);
        var user = userRepository.getReferenceById(userId);
        var comment = new Comment();
        comment.setVideo(video);
        comment.setUser(user);
        comment.setCommentedAt(LocalDateTime.now());
        comment.setContent(content);
        comment.setIsReply(isReply);
        if (isReply && commentId != null) {
            var parentComment = commentRepository.getReferenceById(commentId);
            comment.setParent(parentComment);
        }
        commentRepository.save(comment);
    }

    @Override
    public void likeComment(UUID commentId, UUID userId, boolean isLike) {
        var user = userRepository.getReferenceById(userId);
        var comment = commentRepository.getReferenceById(commentId);
        var commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLike.setLikedAt(LocalDateTime.now());
        commentLike.setIsLike(isLike);
        commentLikeRepository.save(commentLike);
    }
}