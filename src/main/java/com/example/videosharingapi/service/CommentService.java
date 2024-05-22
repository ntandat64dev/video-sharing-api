package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentDto postComment(CommentDto commentDto);

    CommentDto getCommentById(String id);

    PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable);

    PageResponse<CommentDto> getRepliesByCommentId(String commentId, Pageable pageable);
}
