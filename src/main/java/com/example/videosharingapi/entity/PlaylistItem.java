package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "playlistId", "priority" }))
public class PlaylistItem extends AuditableEntity {

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class PlaylistItemId implements Serializable {
        private String playlistId;
        private String videoId;
    }

    @EmbeddedId
    private PlaylistItemId id = new PlaylistItemId();

    @Column(nullable = false)
    private Byte priority;

    @ManyToOne
    @MapsId("playlistId")
    private Playlist playlist;

    @ManyToOne
    @MapsId("videoId")
    private Video video;
}
