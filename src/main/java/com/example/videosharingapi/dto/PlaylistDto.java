package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.group.Create;
import com.example.videosharingapi.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class PlaylistDto {

    @Getter
    @Setter
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

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @Getter
    @Setter
    @Builder
    public static final class Status {

        @NotBlank
        @Pattern(regexp = "(?i)(private|public)")
        private String privacy;

        private Boolean isDefaultPlaylist;
    }

    @Getter
    @Setter
    public static final class ContentDetails {

        private Long itemCount;
    }

    @Null(groups = Create.class)
    @NotNull(groups = Update.class)
    @IdExists(entity = Playlist.class, groups = Update.class)
    private String id;

    @Valid
    @NotNull
    private PlaylistDto.Snippet snippet;

    @Valid
    @NotNull
    private PlaylistDto.Status status;

    @Valid
    private PlaylistDto.ContentDetails contentDetails;
}