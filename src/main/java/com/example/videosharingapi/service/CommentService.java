package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    List<CommentDto> getCommentsByVideoId(UUID videoId);

    CommentDto postComment(CommentDto commentDto);
}
