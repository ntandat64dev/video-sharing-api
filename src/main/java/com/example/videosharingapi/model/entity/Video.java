package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.List;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "video_thumbnail",
            joinColumns = @JoinColumn(name = "video_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "thumbnail_id", nullable = false)
    )
    private List<Thumbnail> thumbnails;

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

    @ManyToMany
    @JoinTable(
            name = "video_hashtag",
            joinColumns = @JoinColumn(name = "video_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id", nullable = false)
    )
    private List<Hashtag> hashtags;

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
