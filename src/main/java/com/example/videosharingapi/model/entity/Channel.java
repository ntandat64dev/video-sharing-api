package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Channel extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 64, nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "channel_thumbnail",
            joinColumns = @JoinColumn(name = "channel_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "thumbnail_id", nullable = false)
    )
    private List<Thumbnail> thumbnails;
}
