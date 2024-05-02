package com.example.videosharingapi.service.impl;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.mapper.CommentMapper;
import com.example.videosharingapi.repository.CommentRepository;
import com.example.videosharingapi.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

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
