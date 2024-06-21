package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "playlistId", "priority" }))
public class PlaylistItem extends AuditableEntity {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Embeddable
    public static class PlaylistItemId implements Serializable {
        private String playlistId;
        private String videoId;
    }

    @EmbeddedId
    private PlaylistItemId id = new PlaylistItemId();

    @Column(nullable = false)
    private Long priority;

    @ManyToOne
    @MapsId("playlistId")
    private Playlist playlist;

    @ManyToOne
    @MapsId("videoId")
    private Video video;
}
