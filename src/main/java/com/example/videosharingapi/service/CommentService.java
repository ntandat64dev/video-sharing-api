package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getCommentsByVideoId(String videoId);

    CommentDto postComment(CommentDto commentDto);
}
