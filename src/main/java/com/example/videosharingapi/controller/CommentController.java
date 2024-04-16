package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/top-level")
    public ResponseEntity<CommentDto> getMostLikeComment(UUID videoId) {
        return new ResponseEntity<>(commentService.getTopLevelComment(videoId), HttpStatus.OK);
    }
}
