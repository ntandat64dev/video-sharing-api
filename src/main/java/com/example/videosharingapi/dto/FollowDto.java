package com.example.videosharingapi.dto;

import com.example.videosharingapi.config.validation.FollowExistsConstraint;
import com.example.videosharingapi.config.validation.IdExistsConstraint;
import com.example.videosharingapi.config.validation.group.OnCreate;
import com.example.videosharingapi.config.validation.SelfFollowConstraint;
import com.example.videosharingapi.model.entity.Follow;
import com.example.videosharingapi.model.entity.Thumbnail;
import com.example.videosharingapi.model.entity.User;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@SelfFollowConstraint(groups = OnCreate.class)
@FollowExistsConstraint(groups = OnCreate.class)
public class FollowDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private LocalDateTime publishedAt;

        @IdExistsConstraint(entity = User.class)
        private UUID userId;

        private String username;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @Getter
    @Setter
    @Builder
    public static final class FollowerSnippet {

        @IdExistsConstraint(entity = User.class)
        private UUID userId;

        private Map<Thumbnail.Type, ThumbnailDto> thumbnails;
    }

    @IdExistsConstraint(entity = Follow.class)
    private UUID id;

    @Valid
    private Snippet snippet;

    @Valid
    private FollowerSnippet followerSnippet;
}
