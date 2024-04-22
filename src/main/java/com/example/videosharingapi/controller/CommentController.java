package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.service.impl.CommentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comments")
@Validated
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentServiceImpl commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByVideoID(UUID videoId) {
        var response = commentService.getCommentsByVideoId(videoId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentDto> postComment(@RequestBody CommentDto commentDto) {
        var response = commentService.postComment(commentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/top-level")
    public ResponseEntity<CommentDto> getTopLevelComment(UUID videoId) {
        return new ResponseEntity<>(commentService.getTopLevelComment(videoId), HttpStatus.OK);
    }
}
