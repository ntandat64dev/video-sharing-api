package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.ValidFile;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.RatingResponse;
import com.example.videosharingapi.payload.response.ViewResponse;
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

    @GetMapping("/{videoId}")
    public ResponseEntity<VideoDto> getVideoById(@PathVariable UUID videoId) {
        var response = videoService.getVideoById(videoId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<VideoDto>> getRecommendVideos(UUID userId) {
        var response = videoService.getRecommendVideos(userId);
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

    @PostMapping("/view")
    public ResponseEntity<ViewResponse> postView(@RequestBody @Valid ViewRequest viewRequest) {
        var viewResponse = videoService.viewVideo(viewRequest);
        return new ResponseEntity<>(viewResponse, HttpStatus.OK);
    }

    @PostMapping("/rate")
    public ResponseEntity<Void> rate(@RequestBody @Valid RatingRequest ratingRequest) {
        videoService.rateVideo(ratingRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/rate")
    public ResponseEntity<RatingResponse> getRating(UUID videoId, UUID userId) {
        return new ResponseEntity<>(videoService.getRating(videoId, userId), HttpStatus.OK);
    }
}
