package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable);

    CommentDto postComment(CommentDto commentDto);
}
