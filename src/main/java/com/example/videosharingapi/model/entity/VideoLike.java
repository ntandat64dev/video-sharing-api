package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
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
    @Embeddable
    public static class VideoLikeId implements Serializable {
        private UUID id;
        private UUID userId;
    }

    @EmbeddedId
    private VideoLikeId id = new VideoLikeId();

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0", nullable = false)
    private Integer isLike;

    @Column(nullable = false)
    private LocalDateTime likedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Video video;

    @ManyToOne
    @MapsId("userId")
    private User user;
}

