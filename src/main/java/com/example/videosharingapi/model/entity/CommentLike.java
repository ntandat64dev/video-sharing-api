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
    @EqualsAndHashCode
    @Embeddable
    public static class CommentLikeId implements Serializable {
        @GeneratedValue
        private UUID id;
        private UUID userId;
    }

    @EmbeddedId
    private CommentLikeId commentLikeId = new CommentLikeId();

    @Column(nullable = false)
    private Boolean isLike;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Comment comment;

    @ManyToOne
    @MapsId("userId")
    private User user;
}
