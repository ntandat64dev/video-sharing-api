package com.example.videosharingapi.dto;

import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.validation.IdExists;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class FollowDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        @IdExists(entity = User.class)
        private String userId;

        private String username;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @Getter
    @Setter
    @Builder
    public static final class FollowerSnippet {

        @IdExists(entity = User.class)
        private String userId;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    private String id;

    @Valid
    private Snippet snippet;

    @Valid
    private FollowerSnippet followerSnippet;
}
