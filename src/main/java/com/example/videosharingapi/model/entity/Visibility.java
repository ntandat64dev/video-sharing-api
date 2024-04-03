package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Visibility extends AuditableEntity {

    public enum VisibilityLevel {
        PUBLIC, PRIVATE
    }

    public Visibility(VisibilityLevel visibilityLevel) {
        this.level = visibilityLevel;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "visibility_level", length = 64, nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private VisibilityLevel level;
}
