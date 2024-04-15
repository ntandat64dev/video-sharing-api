package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "id", "title" }))
public class Playlist extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 64, nullable = false)
    private String title;

    @Lob
    @Column(length = 10000)
    private String description;

    private Boolean isUserCreate;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Privacy privacy;
}
