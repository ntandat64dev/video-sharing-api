package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.Playlist;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.Video;
import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.validation.group.Update;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Duration;
import java.util.Map;

@Getter
@Setter
public final class PlaylistItemDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static final class Snippet {

        @NotNull
        @IdExists(entity = Playlist.class)
        private String playlistId;

        @NotNull
        @IdExists(entity = Video.class)
        private String videoId;

        private String title;

        private String description;

        private String videoUrl;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;

        @NotNull(groups = Update.class)
        @Size(groups = Update.class)
        private Long priority;

        private String videoOwnerUsername;

        private String videoOwnerUserId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static final class ContentDetails {

        private Duration duration;
    }

    @Valid
    @NotNull
    private PlaylistItemDto.Snippet snippet;

    @Valid
    private PlaylistItemDto.ContentDetails contentDetails;
}
