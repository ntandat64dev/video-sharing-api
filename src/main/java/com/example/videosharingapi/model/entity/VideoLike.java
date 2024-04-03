package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class VideoLike extends AuditableEntity {

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class VideoLikeId implements Serializable {
        @GeneratedValue
        private UUID id;
        private UUID userId;
    }

    @EmbeddedId
    private VideoLikeId id = new VideoLikeId();

    @Column(nullable = false)
    private Boolean isLike;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Video video;

    @ManyToOne
    @MapsId("userId")
    private User user;
}

