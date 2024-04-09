package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Channel extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 64, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String pictureUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, unique = true)
    private User user;
}
