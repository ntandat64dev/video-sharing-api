package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.group.Create;
import com.example.videosharingapi.validation.group.Save;
import com.example.videosharingapi.validation.group.Update;
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

@Getter
@Setter
public final class VideoDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        @NotNull
        @IdExists(entity = User.class)
        private String userId;

        private String username;

        private String userImageUrl;

        @NotBlank
        private String title;

        private String description;

        @NotNull(groups = Save.class)
        private String videoUrl;

        @Valid
        @NotNull
        private CategoryDto category;

        @Size(min = 1, groups = Save.class)
        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        private List<String> hashtags;

        @NotNull(groups = Create.class)
        private Duration duration;

        private String location;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Status {

        @NotBlank
        @Pattern(regexp = "(?i)(private|public)")
        private String privacy;

        @NotNull
        private Boolean madeForKids;

        @NotNull
        private Boolean ageRestricted;

        @NotNull
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

    @IdExists(entity = Video.class, groups = Update.class)
    private String id;

    @Valid
    @NotNull
    private Snippet snippet;

    @Valid
    @NotNull(groups = Create.class)
    private Status status;

    private Statistic statistic;
}
