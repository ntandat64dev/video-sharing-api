package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Lob
    @Column(length = 10000, nullable = false)
    private String text;

    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;

    private LocalDateTime updatedAt;

    @ManyToOne
    private Comment parent;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Video video;
}
