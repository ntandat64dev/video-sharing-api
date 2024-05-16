package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.exception.AppException;
import com.example.videosharingapi.exception.ErrorCode;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
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

    @Override
    public PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable) {
        var commentDtoPage = commentRepository.findByVideoIdAndParentIsNull(videoId, pageable)
                .map(commentMapper::toCommentDto);
        return new PageResponse<>(commentDtoPage);
    }

    @Override
    @Transactional
    public CommentDto postComment(CommentDto commentDto) {
        if (!userService.getAuthenticatedUser().getUserId().equals(commentDto.getSnippet().getAuthorId())) {
            // If authenticated user ID is not the author of the comment.
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        var comment = commentMapper.toComment(commentDto);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }
}
