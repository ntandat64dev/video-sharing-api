package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        private String commentId;
    }

    @EmbeddedId
    private CommentRatingId id = new CommentRatingId();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("commentId")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}