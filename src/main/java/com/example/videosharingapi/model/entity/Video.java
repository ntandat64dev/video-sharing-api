package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Video extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Integer durationSec;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @OneToMany(mappedBy = "video")
    private Set<VideoTag> videoTags;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private VideoSpec videoSpec;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;
}
