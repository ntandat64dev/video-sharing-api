package com.example.videosharingapi.controller;

import com.example.videosharingapi.config.validation.ValidFile;
import com.example.videosharingapi.payload.VideoDto;
import com.example.videosharingapi.payload.request.RatingRequest;
import com.example.videosharingapi.payload.request.ViewRequest;
import com.example.videosharingapi.payload.response.ViewResponse;
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
        var videoDto = videoService.saveVideo(metadata);
        return new ResponseEntity<>(videoDto, HttpStatus.CREATED);
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

    @PostMapping("/comment")
    public ResponseEntity<String> commentVideo(UUID videoId, UUID userId, String content) {
        videoService.comment(videoId, userId, content);
        return new ResponseEntity<>("Video commented.", HttpStatus.OK);
    }

    @PostMapping("/comment/reply")
    public ResponseEntity<String> replyCommentVideo(UUID videoId, UUID userId, UUID commentId, String content) {
        videoService.reply(videoId, userId, commentId, content);
        return new ResponseEntity<>("Comment replied.", HttpStatus.OK);
    }

    @PostMapping("/comment/like")
    public ResponseEntity<String> rateComment(UUID commentId, UUID userId) {
        videoService.rateComment(commentId, userId, true);
        return new ResponseEntity<>("Comment liked..", HttpStatus.OK);
    }

    @PostMapping("/comment/dislike")
    public ResponseEntity<String> dislikeComment(UUID commentId, UUID userId) {
        videoService.rateComment(commentId, userId, true);
        return new ResponseEntity<>("Comment liked..", HttpStatus.OK);
    }
}
