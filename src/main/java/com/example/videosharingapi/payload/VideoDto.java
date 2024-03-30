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
    @NotBlank(message = "{validation.video.title.required}")
    private String title;
    private String description;
    private String thumbnailUrl;
    private String videoUrl;
    private Integer durationSec;
    private LocalDateTime uploadDate;
    private Set<String> tags;
    @NotBlank(message = "{validation.video.visibility.required}")
    @Pattern(regexp = "(?i)(private|public)", message = "{validation.video.visibility.in-range}")
    private String visibility;
    @NotNull(message = "{validation.video.user-id.required}")
    private UUID userId;
}
