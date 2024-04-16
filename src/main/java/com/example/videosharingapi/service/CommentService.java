package com.example.videosharingapi.service;

import com.example.videosharingapi.dto.CommentDto;

import java.util.UUID;

public interface CommentService {
    CommentDto getTopLevelComment(UUID videoId);
}
