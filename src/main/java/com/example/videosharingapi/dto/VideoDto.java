package com.example.videosharingapi.dto;

import com.example.videosharingapi.model.entity.Thumbnail;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public final class VideoDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Snippet {
        private LocalDateTime publishedAt;

        @NotNull(message = "{validation.user.id.required}")
        private UUID userId;

        private String username;

        private String userImageUrl;

        @NotBlank(message = "{validation.video.title.required}")
        private String title;

        private String description;

        private String videoUrl;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        private List<String> hashtags;

        private Duration duration;

        private String location;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Status {

        @NotBlank(message = "{validation.video.privacy.required}")
        @Pattern(regexp = "(?i)(private|public)", message = "{validation.video.privacy.in-range}")
        private String privacy;

        @NotNull(message = "{validation.video.for-kids.required}")
        private Boolean madeForKids;

        @NotNull(message = "{validation.video.age-restricted.required}")
        private Boolean ageRestricted;

        @NotNull(message = "{validation.video.comment-allowed.required}")
        private Boolean commentAllowed;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Statistic {
        private Long viewCount;

        private Long likeCount;

        private Long dislikeCount;

        private Long commentCount;

        private Long downloadCount;
    }

    private UUID id;

    @Valid
    private Snippet snippet;

    @Valid
    private Status status;

    private Statistic statistic;
}
