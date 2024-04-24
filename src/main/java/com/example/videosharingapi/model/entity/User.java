package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class User extends AuditableEntity {

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDate dateOfBirth;

    @Column(length = 32, unique = true)
    private String phoneNumber;

    private Byte gender;

    @Column(length = 64)
    private String country;

    @Column(length = 64, nullable = false)
    private String username;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false, updatable = false)
    private LocalDateTime publishedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "user_thumbnail",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "thumbnail_id", nullable = false)
    )
    private List<Thumbnail> thumbnails;
}
