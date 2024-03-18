package com.example.videosharingapi.controller;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.service.StorageService;
import com.example.videosharingapi.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {
    private final VideoService videoService;
    private final StorageService storageService;

    public VideoController(VideoService videoService, StorageService storageService) {
        this.videoService = videoService;
        this.storageService = storageService;
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoDto>> getVideos() {
        var response = videoService.getAllVideos();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/videos")
    public ResponseEntity<VideoDto> uploadVideo(@RequestParam MultipartFile videoFile,
                                                @RequestPart VideoDto metadata) {
        var storedVideo = storageService.store(videoFile);
        metadata.setThumbnailUrl(storedVideo.getThumbnailUrl());
        metadata.setVideoUrl(storedVideo.getVideoUrl());
        var videoDto = videoService.save(metadata);
        return new ResponseEntity<>(videoDto, HttpStatus.CREATED);
    }
}
