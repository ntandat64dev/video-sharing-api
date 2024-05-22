package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.NotificationDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.NotificationObject;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.service.NotificationService;
import com.example.videosharingapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    private final NotificationService notificationService;

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
    public CommentDto getCommentById(String id) {
        return commentRepository.findById(id)
                .map(commentMapper::toCommentDto)
                .orElseThrow();
    }

    @Override
    public PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable) {
        var commentDtoPage = commentRepository.findAllByVideoIdAndParentIsNull(videoId, pageable)
                .map(commentMapper::toCommentDto);
        return new PageResponse<>(commentDtoPage);
    }

    @Override
    public PageResponse<CommentDto> getRepliesByCommentId(String commentId, Pageable pageable) {
        var commentDtoPage = commentRepository.findAllByParentId(commentId, pageable)
                .map(commentMapper::toCommentDto);
        return new PageResponse<>(commentDtoPage);
    }
}
