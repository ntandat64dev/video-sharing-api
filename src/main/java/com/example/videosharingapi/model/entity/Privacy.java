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
public class Privacy extends AuditableEntity {

    public enum Status {
        PUBLIC, PRIVATE
    }

    public Privacy(Status status) {
        this.status = status;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Status status;
}
