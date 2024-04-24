package com.example.videosharingapi.dto;

import com.example.videosharingapi.model.entity.Thumbnail;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class FollowDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        private UUID userId;

        private String username;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @Getter
    @Setter
    @Builder
    public static final class FollowerSnippet {

        private UUID userId;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    private UUID id;

    private Snippet snippet;

    private FollowerSnippet followerSnippet;
}
