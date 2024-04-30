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
public class VideoRating extends AuditableEntity {

    public enum Rating {
        LIKE, DISLIKE
    }

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class VideoRatingId implements Serializable {
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        private String videoId;
    }

    @EmbeddedId
    private VideoRatingId id = new VideoRatingId();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Rating rating;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}