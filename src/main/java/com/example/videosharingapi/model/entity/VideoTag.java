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
public class VideoTag extends AuditableEntity {

    @Getter
    @EqualsAndHashCode
    @Embeddable
    public static class VideoTagId implements Serializable {
        private UUID videoId;
        private UUID tagId;
    }

    @EmbeddedId
    private VideoTagId id = new VideoTagId();

    @ManyToOne
    @MapsId("videoId")
    private Video video;

    @ManyToOne
    @MapsId("tagId")
    private Tag tag;
}
