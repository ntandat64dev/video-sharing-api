package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class FcmMessageToken extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, updatable = false, unique = true)
    private String token;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
}
