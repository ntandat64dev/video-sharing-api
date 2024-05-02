package com.example.videosharingapi.dto;

import com.example.videosharingapi.validation.IdExists;
import com.example.videosharingapi.entity.Comment;
import com.example.videosharingapi.entity.User;
import com.example.videosharingapi.entity.Video;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public final class CommentDto {

    @Getter
    @Setter
    @Builder
    public static final class Snippet {

        @NotNull
        @IdExists(entity = Video.class)
        private String videoId;

        @NotNull
        @IdExists(entity = User.class)
        private String authorId;

        private String authorDisplayName;

        private String authorProfileImageUrl;

        @NotNull
        @NotBlank
        private String text;

        @IdExists(entity = Comment.class)
        private String parentId;

        private LocalDateTime publishedAt;

        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    public static final class Statistic {

        private Long likeCount;

        private Long dislikeCount;

        private Long replyCount;
    }

    @IdExists(entity = Comment.class)
    private String id;

    @Valid
    private Snippet snippet;

    private Statistic statistic;
}
