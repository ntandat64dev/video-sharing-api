package com.example.videosharingapi.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CommentDto {

    @Getter
    @Setter
    public static class Snippet {

        private UUID videoId;

        private UUID authorId;

        private String authorDisplayName;

        private String authorProfileImageUrl;

        private String text;

        private UUID parentId;

        private Integer likeCount;

        private Integer dislikeCount;

        private LocalDateTime publishedAt;

        private LocalDateTime updatedAt;
    }

    private UUID id;

    private Snippet snippet;
}
