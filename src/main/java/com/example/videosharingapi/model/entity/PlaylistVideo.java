package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
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
    @Embeddable
    public static class PlaylistVideoKey implements Serializable {
        private UUID playlistId;
        private UUID videoId;
    }

    @EmbeddedId
    private PlaylistVideoKey id = new PlaylistVideoKey();

    @Column(columnDefinition = "SMALLINT", nullable = false)
    private Integer priority;

    @ManyToOne
    @MapsId("playlistId")
    private Playlist playlist;

    @ManyToOne
    @MapsId("videoId")
    private Video video;
}
