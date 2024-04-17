package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.ValidFile;
import com.example.videosharingapi.dto.VideoDto;
import com.example.videosharingapi.dto.VideoRatingDto;
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

    @GetMapping("/category/all")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(UUID userId) {
        var response = videoService.getVideosByAllCategories(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/related")
    public ResponseEntity<List<VideoDto>> getRelatedVideos(UUID videoId, UUID userId) {
        var response = videoService.getRelatedVideos(videoId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(@RequestParam @ValidFile MultipartFile videoFile,
                                                @RequestPart @Valid VideoDto metadata) {
        var response = videoService.saveVideo(videoFile, metadata);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/rate")
    public ResponseEntity<VideoRatingDto> getRating(UUID videoId, UUID userId) {
        return new ResponseEntity<>(videoService.getRating(videoId, userId), HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<?> rateVideo(UUID videoId, UUID userId, String rating) {
        videoService.rateVideo(videoId, userId, rating);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
