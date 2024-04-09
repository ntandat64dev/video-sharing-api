package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class CommentRating extends AuditableEntity {

    public enum Rating {
        LIKE, DISLIKE
    }

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class CommentRatingId implements Serializable {
        @GeneratedValue
        private UUID id;
        private UUID commentId;
    }

    @EmbeddedId
    private CommentRatingId id = new CommentRatingId();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(nullable = false)
    private LocalDateTime ratedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}