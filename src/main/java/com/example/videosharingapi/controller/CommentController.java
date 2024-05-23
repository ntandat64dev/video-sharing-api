package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.CommentDto;
import com.example.videosharingapi.dto.CommentRatingDto;
import com.example.videosharingapi.dto.response.PageResponse;
import com.example.videosharingapi.entity.Comment;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.service.CommentService;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.group.Create;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@Validated
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> postComment(
            @RequestBody @Validated({ Default.class, Create.class })
            CommentDto commentDto
    ) {
        var response = commentService.postComment(commentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteComment(@IdExists(entity = Comment.class) String id) {
        commentService.deleteCommentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(
            @PathVariable @IdExists(entity = Comment.class) String id
    ) {
        var response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByVideoId(
            @NotNull @IdExists(entity = Video.class) String videoId,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var response = commentService.getCommentsByVideoId(videoId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/replies")
    public ResponseEntity<PageResponse<CommentDto>> getRepliesByCommentId(
            @NotNull @IdExists(entity = Comment.class) String commentId,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        var response = commentService.getRepliesByCommentId(commentId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/rate/mine")
    public ResponseEntity<CommentRatingDto> rateComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @IdExists(entity = Comment.class) String commentId,
            String rating
    ) {
        var response = commentService.rateComment(commentId, user.getUserId(), rating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rate/mine")
    public ResponseEntity<CommentRatingDto> getMyRatingAboutComment(
            @AuthenticationPrincipal AuthenticatedUser user,
            @IdExists(entity = Comment.class) String commentId
    ) {
        var response = commentService.getRating(commentId, user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rates/mine")
    public ResponseEntity<PageResponse<CommentRatingDto>> getMyCommentRatingsOfVideo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @IdExists(entity = Video.class) String videoId,
            Pageable pageable
    ) {
        var response = commentService.getRatingsOfVideo(videoId, user.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}
