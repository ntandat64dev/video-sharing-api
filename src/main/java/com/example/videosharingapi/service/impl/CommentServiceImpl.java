package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper, CommentRepository commentRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentDto> getCommentsByVideoId(String videoId) {
        return commentRepository.findByVideoIdAndParentIsNull(videoId).stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public CommentDto postComment(CommentDto commentDto) {
        var comment = commentMapper.toComment(commentDto);
        commentRepository.save(comment);
        return commentMapper.toCommentDto(comment);
    }
}
