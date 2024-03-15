package com.example.videosharingapi.controller;

import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.service.VideoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VideoController {
    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoDto>> getVideos() {
        var response = videoService.getAllVideos();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
