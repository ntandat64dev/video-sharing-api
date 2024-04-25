package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Thumbnail extends AuditableEntity {

    public enum Type {
        DEFAULT, MEDIUM, HIGH, STANDARD
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;
}
