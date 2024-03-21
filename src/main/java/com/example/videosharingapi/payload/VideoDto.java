package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@Setter
public final class VideoDto {
    private UUID id;
    @NotBlank(message = "Video title is required")
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private UserDto user;
}
