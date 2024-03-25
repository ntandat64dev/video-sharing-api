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
public class VideoTag extends AuditableEntity {

    @Getter
    @Embeddable
    public static class VideoTagKey implements Serializable {
        private UUID videoId;
        private UUID tagId;
    }

    @EmbeddedId
    private VideoTagKey id = new VideoTagKey();

    @ManyToOne
    @MapsId("videoId")
    private Video video;

    @ManyToOne
    @MapsId("tagId")
    private Tag tag;
}
