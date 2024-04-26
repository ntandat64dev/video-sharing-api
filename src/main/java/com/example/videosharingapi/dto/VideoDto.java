package com.example.videosharingapi.dto;

import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.config.validation.group.Save;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

        @NotNull(message = "'userId' {jakarta.validation.constraints.NotNull.message}")
        @IdExistsConstraint(entity = User.class)
        private UUID userId;

        private String username;

        private String userImageUrl;

        @NotBlank(message = "'title' {jakarta.validation.constraints.NotBlank.message}")
        private String title;

        private String description;

        @NotNull(groups = Save.class)
        private String videoUrl;

        @Size(min = 1, groups = Save.class)
        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        private List<String> hashtags;

        @NotNull(groups = Save.class)
        private Duration duration;

        private String location;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Status {

        @NotBlank(message = "'privacy' {jakarta.validation.constraints.NotNull.message}")
        @Pattern(
                regexp = "(?i)(private|public)",
                message = "'privacy' {jakarta.validation.constraints.Pattern.message}")
        private String privacy;

        @NotNull(message = "'madeForKids' {jakarta.validation.constraints.NotNull.message}")
        private Boolean madeForKids;

        @NotNull(message = "'ageRestricted' {jakarta.validation.constraints.NotNull.message}")
        private Boolean ageRestricted;

        @NotNull(message = "'commentAllowed' {jakarta.validation.constraints.NotNull.message}")
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
    @NotNull(message = "'snippet' {jakarta.validation.constraints.NotNull.message}")
    private Snippet snippet;

    @Valid
    @NotNull(message = "'status' {jakarta.validation.constraints.NotNull.message}")
    private Status status;

    private Statistic statistic;
}
