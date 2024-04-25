package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.service.impl.CommentServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
    public ResponseEntity<List<CommentDto>> getCommentsByVideoID(
            @NotNull(message = "Video ID {jakarta.validation.constraints.NotNull.message}")
            @IdExistsConstraint(entity = Video.class)
            UUID videoId
    ) {
        var response = commentService.getCommentsByVideoId(videoId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CommentDto> postComment(@RequestBody @Valid CommentDto commentDto) {
        var response = commentService.postComment(commentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
