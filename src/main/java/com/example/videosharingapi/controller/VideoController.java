package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.FileUploadConstraint;
import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/videos")
@Validated
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<VideoDto> getVideo(@IdExistsConstraint(entity = Video.class) String videoId) {
        var response = videoService.getVideoById(videoId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/category/all")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(@IdExistsConstraint(entity = User.class) String userId) {
        var response = videoService.getVideosByAllCategories(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/related")
    public ResponseEntity<List<VideoDto>> getRelatedVideos(
            @IdExistsConstraint(entity = Video.class)
            String videoId,
            @IdExistsConstraint(entity = User.class)
            String userId
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
            String videoId,
            @IdExistsConstraint(entity = User.class)
            String userId
    ) {
        return new ResponseEntity<>(videoService.getRating(videoId, userId), HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<VideoRatingDto> rateVideo(
            @IdExistsConstraint(entity = Video.class)
            String videoId,
            @IdExistsConstraint(entity = User.class)
            String userId,
            String rating
    ) {
        var response = videoService.rateVideo(videoId, userId, rating);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
