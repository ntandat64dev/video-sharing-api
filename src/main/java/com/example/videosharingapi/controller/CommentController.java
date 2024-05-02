package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comments")
@Validated
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsByVideoID(
            @NotNull
            @IdExists(entity = Video.class)
            String videoId
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
