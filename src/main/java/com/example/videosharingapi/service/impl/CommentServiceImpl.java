package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.CommentRatingDto;
import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.CommentRating;
import com.example.videosharingapi.entity.NotificationObject;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.mapper.CommentRatingMapper;
import com.example.videosharingapi.repository.CommentRatingRepository;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.repository.UserRepository;
import com.example.videosharingapi.repository.VideoStatisticRepository;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.service.NotificationService;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentRatingRepository commentRatingRepository;
    private final VideoStatisticRepository videoStatisticRepository;

    private final NotificationService notificationService;
    private final UserService userService;

    private final CommentMapper commentMapper;
    private final CommentRatingMapper commentRatingMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto postComment(CommentDto commentDto) {
        var snippet = commentDto.getSnippet();

        if (!userService.getAuthenticatedUser().getUserId().equals(snippet.getAuthorId())) {
            // If authenticated user ID is not the author of the comment.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (snippet.getParentId() != null) {
            var parentComment = commentRepository.findById(snippet.getParentId()).orElseThrow();
            if (parentComment.getParent() != null) {
                // Nested reply is not supported.
                throw new AppException(ErrorCode.NESTED_REPLY);
            }
        }

        var comment = commentMapper.toComment(commentDto);
        commentRepository.save(comment);

        // Create notification.
        var actionType = snippet.getParentId() == null ? 3 : 4;
        var notificationDto = new NotificationDto();
        notificationDto.setSnippet(NotificationDto.Snippet.builder()
                .actionType(actionType)
                .actorId(comment.getUser().getId())
                .objectType(NotificationObject.ObjectType.COMMENT)
                .objectId(comment.getId())
                .build());
        notificationService.createNotification(notificationDto);

        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteCommentById(String id) {
        var user = userService.getAuthenticatedUser();
        var comment = commentRepository.findById(id).orElseThrow();
        if (!comment.getUser().getId().equals(user.getUserId())) throw new AppException(ErrorCode.FORBIDDEN);

        // Delete CommentRatings.
        commentRatingRepository.deleteByCommentId(id);
        commentRatingRepository.deleteByCommentParentId(id);

        // Delete reply comments.
        var rows = commentRepository.deleteByParentId(id);

        // Delete Comment.
        commentRepository.deleteById(id);

        // Update VideoStatistic.
        var videoStat = videoStatisticRepository.findById(comment.getVideo().getId()).orElseThrow();
        videoStat.setCommentCount(videoStat.getCommentCount() - (1 + rows));
        videoStatisticRepository.save(videoStat);

        // Delete related notifications.
        notificationService.deleteRelatedNotifications(id);
    }

    @Override
    public CommentDto getCommentById(String id) {
        return commentRepository.findById(id)
                .map(commentMapper::toCommentDto)
                .orElseThrow();
    }

    @Override
    public PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable) {
        var commentDtoPage = commentRepository
                .findAllByVideoIdAndParentIsNull(videoId, pageable)
                .map(commentMapper::toCommentDto);
        return new PageResponse<>(commentDtoPage);
    }

    @Override
    public PageResponse<CommentDto> getRepliesByCommentId(String commentId, Pageable pageable) {
        var commentDtoPage = commentRepository
                .findAllByParentId(commentId, pageable)
                .map(commentMapper::toCommentDto);
        return new PageResponse<>(commentDtoPage);
    }

    @Override
    @Transactional
    public CommentRatingDto rateComment(String commentId, String userId, String rating) {
        var comment = commentRepository.findById(commentId).orElseThrow();
        var user = userRepository.findById(userId).orElseThrow();

        var commentRating = commentRatingRepository.findByUserIdAndCommentId(user.getId(), comment.getId());

        if (commentRating == null && Objects.equals(rating, CommentRatingDto.NONE)) {
            // If the user rates NONE but there is no rating yet, return NONE rating.
            return commentRatingMapper.createNoneRatingDto(commentId, userId);
        }

        if (commentRating != null && commentRating.getRating().name().equalsIgnoreCase(rating)) {
            // If the user rates the same rating, do nothing and return.
            return commentRatingMapper.toCommentRatingDto(commentRating);
        }

        if (commentRating != null) {
            if (Objects.equals(rating, VideoRatingDto.NONE)) {
                // If the user rates NONE, then delete the rating and return NONE.
                commentRatingRepository.delete(commentRating);
                return commentRatingMapper.createNoneRatingDto(commentId, userId);
            } else {
                // else update the current rating.
                commentRating.setRating(CommentRating.Rating.valueOf(rating.toUpperCase()));
                commentRating.setPublishedAt(LocalDateTime.now());
                commentRatingRepository.save(commentRating);
            }
        } else {
            // If this is the first time the user has rated the comment,
            // then create a new rating and save.
            commentRating = new CommentRating();
            commentRating.setComment(comment);
            commentRating.setUser(user);
            commentRating.setRating(CommentRating.Rating.valueOf(rating.toUpperCase()));
            commentRating.setPublishedAt(LocalDateTime.now());
            commentRatingRepository.save(commentRating);
        }
        return commentRatingMapper.toCommentRatingDto(commentRating);
    }

    @Override
    public CommentRatingDto getRating(String commentId, String userId) {
        var commentRating = commentRatingRepository.findByUserIdAndCommentId(userId, commentId);
        if (commentRating == null) return commentRatingMapper.createNoneRatingDto(commentId, userId);
        return commentRatingMapper.toCommentRatingDto(commentRating);
    }

    @Override
    public PageResponse<CommentRatingDto> getRatingsOfVideo(String videoId, String userId, Pageable pageable) {
        var commentDtoPage = commentRatingRepository
                .findAllByUserIdAndCommentVideoId(userId, videoId, pageable)
                .map(commentRatingMapper::toCommentRatingDto);
        return new PageResponse<>(commentDtoPage);
    }
}
