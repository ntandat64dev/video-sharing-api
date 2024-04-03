package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.ValidFile;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.service.StorageService;
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
@RequestMapping("/api/videos")
@Validated
public class VideoController {
    private final VideoService videoService;
    private final StorageService storageService;

    public VideoController(VideoService videoService, StorageService storageService) {
        this.videoService = videoService;
        this.storageService = storageService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(@PathVariable UUID userId) {
        var response = videoService.getRecommendVideos(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<VideoDto>> getVideos() {
        var response = videoService.getAllVideos();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<VideoDto> uploadVideo(@RequestParam @ValidFile MultipartFile videoFile,
                                                @RequestPart @Valid VideoDto metadata) {
        var storedVideo = storageService.store(videoFile);
        metadata.setThumbnailUrl(storedVideo.getThumbnailUrl());
        metadata.setVideoUrl(storedVideo.getVideoUrl());
        metadata.setDurationSec(storedVideo.getDurationSec());
        var videoDto = videoService.save(metadata);
        return new ResponseEntity<>(videoDto, HttpStatus.CREATED);
    }
}
