package com.example.videosharingapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Tag extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(length = 64, nullable = false, unique = true)
    private String tag;
}
