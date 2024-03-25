package com.example.videosharingapi.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class VideoSpec extends AuditableEntity {

    @Id
    private UUID id;

    private Long viewCount;

    private Long likeCount;

    private Long dislikeCount;

    private Long commentCount;

    private Long downloadCount;

    @OneToOne
    @MapsId
    private Video video;
}
