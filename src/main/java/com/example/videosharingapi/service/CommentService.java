package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.CommentRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentDto postComment(CommentDto commentDto);

    void deleteCommentById(String id);

    CommentDto getCommentById(String id);

    PageResponse<CommentDto> getCommentsByVideoId(String videoId, Pageable pageable);

    PageResponse<CommentDto> getRepliesByCommentId(String commentId, Pageable pageable);

    CommentRatingDto rateComment(String commentId, String userId, String rating);

    CommentRatingDto getRating(String commentId, String userId);

    PageResponse<CommentRatingDto> getRatingsOfVideo(String videoId, String userId, Pageable pageable);
}
