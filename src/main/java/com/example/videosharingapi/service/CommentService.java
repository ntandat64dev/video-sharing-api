package com.example.videosharingapi.service;

import com.example.videosharingapi.payload.CommentDto;

import java.util.UUID;

public interface CommentService {
    CommentDto getMostLikeComment(UUID videoId);
}
