package com.example.videosharingapi.payload;

public record VideoDto(long id, String title, String description, String thumbnailUrl, String videoUrl, UserDto user) {
}
