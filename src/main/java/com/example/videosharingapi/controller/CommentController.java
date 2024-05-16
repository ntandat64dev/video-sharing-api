package com.example.videosharingapi.controller;

import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@Validated
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByVideoID(
            @NotNull @IdExists(entity = Video.class) String videoId,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var response = commentService.getCommentsByVideoId(videoId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentDto> postComment(@RequestBody @Valid CommentDto commentDto) {
        var response = commentService.postComment(commentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
