package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class ViewHistory extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private Integer viewedDuration;

    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Video video;
}
