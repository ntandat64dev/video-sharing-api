package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "playlistId", "priority" }))
public class PlaylistVideo extends AuditableEntity {

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class PlaylistVideoId implements Serializable {
        private UUID playlistId;
        private UUID videoId;
    }

    @EmbeddedId
    private PlaylistVideoId id = new PlaylistVideoId();

    @Column(columnDefinition = "SMALLINT", nullable = false)
    private Integer priority;

    @ManyToOne
    @MapsId("playlistId")
    private Playlist playlist;

    @ManyToOne
    @MapsId("videoId")
    private Video video;
}
