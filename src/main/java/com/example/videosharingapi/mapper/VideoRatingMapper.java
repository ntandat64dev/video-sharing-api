package com.example.videosharingapi.mapper;

import com.example.videosharingapi.dto.VideoRatingDto;
import com.example.videosharingapi.entity.VideoRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VideoRatingMapper {

    @Mapping(target = "videoId", source = "video.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "rating", expression = "java(videoRating.getRating().name().toLowerCase())")
    VideoRatingDto toVideoRatingDto(VideoRating videoRating);

    default VideoRatingDto fromNullVideoRating(String videoId, String userId) {
        var videoRatingDto = new VideoRatingDto();
        videoRatingDto.setVideoId(videoId);
        videoRatingDto.setUserId(userId);
        videoRatingDto.setRating(VideoRatingDto.NONE);
        return videoRatingDto;
    }
}
