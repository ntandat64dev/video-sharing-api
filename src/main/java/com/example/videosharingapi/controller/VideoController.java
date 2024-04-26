package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.FileUploadConstraint;
import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.model.entity.User;
import com.example.videosharingapi.model.entity.Video;
import com.example.videosharingapi.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/videos")
@Validated
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<VideoDto> getVideo(@IdExistsConstraint(entity = Video.class) UUID videoId) {
        var response = videoService.getVideoById(videoId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/all")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(@IdExistsConstraint(entity = User.class) UUID userId) {
        var response = videoService.getVideosByAllCategories(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/related")
    public ResponseEntity<List<VideoDto>> getRelatedVideos(
            @IdExistsConstraint(entity = Video.class)
            UUID videoId,
            @IdExistsConstraint(entity = User.class)
            UUID userId
    ) {
        var response = videoService.getRelatedVideos(videoId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(@RequestParam @FileUploadConstraint MultipartFile videoFile,
                                                @RequestPart @Valid VideoDto metadata) {
        var response = videoService.saveVideo(videoFile, metadata);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/rate")
    public ResponseEntity<VideoRatingDto> getRating(
            @IdExistsConstraint(entity = Video.class)
            UUID videoId,
            @IdExistsConstraint(entity = User.class)
            UUID userId
    ) {
        return new ResponseEntity<>(videoService.getRating(videoId, userId), HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<VideoRatingDto> rateVideo(
            @IdExistsConstraint(entity = Video.class)
            UUID videoId,
            @IdExistsConstraint(entity = User.class)
            UUID userId,
            String rating
    ) {
        var response = videoService.rateVideo(videoId, userId, rating);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
