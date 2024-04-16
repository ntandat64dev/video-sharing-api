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
@Check(constraints = "(made_for_kids = 1 AND age_restricted = 0) OR made_for_kids = 0")
public class Video extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(length = 1000000)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;

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

    @Column(nullable = false)
    private Boolean madeForKids;

    @Column(nullable = false)
    private Boolean ageRestricted;

    @Column(nullable = false)
    private Boolean commentAllowed;

    private String location;

    @ManyToMany
    @JoinTable(
            name = "video_hashtag",
            joinColumns = @JoinColumn(name = "video_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id", nullable = false)
    )
    private List<Hashtag> hashtags;

    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL)
    private VideoStatistic videoStatistic;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Privacy privacy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    public void setVideoStatistic(VideoStatistic videoStatistic) {
        videoStatistic.setVideo(this);
        this.videoStatistic = videoStatistic;
    }
}
