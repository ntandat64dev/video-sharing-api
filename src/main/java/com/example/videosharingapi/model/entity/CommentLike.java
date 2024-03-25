package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class CommentLike extends AuditableEntity {

    @Getter
    @Embeddable
    public static class CommentLikeKey implements Serializable {
        private UUID id;
        private UUID userId;
    }

    @EmbeddedId
    private CommentLikeKey commentLikeKey = new CommentLikeKey();

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    private Integer isLike;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Comment comment;

    @ManyToOne
    @MapsId("userId")
    private User user;
}
