package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.payload.CommentDto;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    public CommentServiceImpl(CommentMapper commentMapper, CommentRepository commentRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getMostLikeComment(UUID videoId) {
        // TODO: Implement
        return commentMapper.toCommentDto(commentRepository.findByVideoId(videoId).stream()
                .findFirst()
                .orElse(null));
    }
}
