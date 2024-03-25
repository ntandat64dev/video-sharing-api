package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Visibility extends AuditableEntity {

    public enum VisibilityLevel {
        PUBLIC, PRIVATE
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 64, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private VisibilityLevel visibilityLevel;
}
