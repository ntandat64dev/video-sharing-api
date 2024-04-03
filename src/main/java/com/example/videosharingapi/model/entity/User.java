package com.example.videosharingapi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;
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

    @Column(name = "phone", length = 32, unique = true)
    private String phoneNumber;

    @Column(columnDefinition = "TINYINT(1)")
    private Integer gender;

    @Column(length = 64)
    private String country;
}
