package com.example.videosharingapi.dto;

import com.example.videosharingapi.config.validation.FollowNotExistsConstraint;
import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.config.validation.SelfFollowConstraint;
import com.example.videosharingapi.entity.Thumbnail;
import com.example.videosharingapi.entity.User;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@SelfFollowConstraint
@FollowNotExistsConstraint
public class FollowDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        @IdExistsConstraint(entity = User.class)
        private String userId;

        private String username;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @Getter
    @Setter
    @Builder
    public static final class FollowerSnippet {

        @IdExistsConstraint(entity = User.class)
        private String userId;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    private String id;

    @Valid
    private Snippet snippet;

    @Valid
    private FollowerSnippet followerSnippet;
}
