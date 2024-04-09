package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Check(constraints = "(for_kids = 1 AND age_restricted = 0) OR for_kids = 0")
public class Video extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(length = 1000000)
    private String description;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Integer durationSec;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate;

    @Column(name = "for_kids", nullable = false)
    private Boolean isMadeForKids;

    @Column(name = "age_restricted", nullable = false)
    private Boolean isAgeRestricted;

    @Column(name = "comment_allowed", nullable = false)
    private Boolean isCommentAllowed;

    private String location;

    @OneToMany(mappedBy = "video")
    private Set<VideoHashtag> videoHashtags;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoSpec videoSpec;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Visibility visibility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    public void setVideoSpec(VideoSpec videoSpec) {
        videoSpec.setVideo(this);
        this.videoSpec = videoSpec;
    }
}
