package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
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

    @Column(columnDefinition = "TEXT")
    private String description;

    private Boolean isUserCreate;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Visibility visibility;
}
