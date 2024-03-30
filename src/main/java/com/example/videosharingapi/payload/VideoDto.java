package com.example.videosharingapi.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
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
    private Integer durationSec;
    private LocalDateTime uploadDate;
    private Set<String> tags;
    @NotBlank(message = "Visibility is required")
    @Pattern(regexp = "(?i)(private|public)", message = "Visibility must either 'private' or 'public'.")
    private String visibility;
    @NotNull(message = "User ID is required")
    private UUID userId;
}
