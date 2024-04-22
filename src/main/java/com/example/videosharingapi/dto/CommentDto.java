package com.example.videosharingapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public final class CommentDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        private UUID videoId;

        private UUID authorId;

        private String authorDisplayName;

        private String authorProfileImageUrl;

        private String text;

        private UUID parentId;

        private LocalDateTime publishedAt;

        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    public static final class Statistic {

        private Integer likeCount;

        private Integer dislikeCount;

        private Integer replyCount;
    }

    private UUID id;

    private Snippet snippet;

    private Statistic statistic;
}
