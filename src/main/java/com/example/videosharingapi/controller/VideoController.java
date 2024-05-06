package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.security.AuthenticatedUser;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.service.VideoService;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.ValidVideoFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
@Validated
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<VideoDto>> getAllVideos() {
        var response = videoService.getAllVideos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoDto> getVideo(@PathVariable @IdExists(entity = Video.class) String videoId) {
        var response = videoService.getVideoById(videoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/all/mine")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(@AuthenticationPrincipal AuthenticatedUser user) {
        var response = videoService.getVideosByAllCategories(user.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/related")
    public ResponseEntity<List<VideoDto>> getRelatedVideos(
            @IdExists(entity = Video.class) String videoId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        var response = videoService.getRelatedVideos(videoId, user.getUserId());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(
            @RequestParam @ValidVideoFile MultipartFile videoFile,
            @RequestPart @Valid VideoDto metadata
    ) {
        var response = videoService.saveVideo(videoFile, metadata);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/rate")
    public ResponseEntity<VideoRatingDto> getRating(
            @IdExists(entity = Video.class) String videoId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        return ResponseEntity.ok(videoService.getRating(videoId, user.getUserId()));
    }

    @PostMapping("/rate")
    public ResponseEntity<VideoRatingDto> rateVideo(
            @IdExists(entity = Video.class) String videoId,
            @AuthenticationPrincipal AuthenticatedUser user,
            String rating
    ) {
        var response = videoService.rateVideo(videoId, user.getUserId(), rating);
        return ResponseEntity.ok(response);
    }
}
