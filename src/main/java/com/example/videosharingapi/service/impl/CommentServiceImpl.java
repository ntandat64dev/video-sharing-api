package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.config.mapper.CommentCommentDtoMapper;
import com.example.videosharingapi.payload.CommentDto;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentCommentDtoMapper commentCommentDtoMapper;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentCommentDtoMapper commentCommentDtoMapper, CommentRepository commentRepository) {
        this.commentCommentDtoMapper = commentCommentDtoMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getMostLikeComment(UUID videoId) {
        // TODO: Implement
        return commentCommentDtoMapper.commentToCommentDto(commentRepository.findByVideoId(videoId).stream()
                .findFirst()
                .orElse(null));
    }
}
