package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private Status status;
}
