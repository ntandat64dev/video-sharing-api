package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class VideoStatistic extends AuditableEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @Column(nullable = false)
    private Long likeCount = 0L;

    @Column(nullable = false)
    private Long dislikeCount = 0L;

    @Column(nullable = false)
    private Long commentCount = 0L;

    @Column(nullable = false)
    private Long downloadCount = 0L;

    @OneToOne
    @MapsId
    private Video video;
}
