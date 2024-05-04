package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class User extends AuditableEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 64, nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private LocalDate dateOfBirth;

    @Column(length = 32, unique = true)
    private String phoneNumber;

    private Byte gender;

    @Column(length = 64)
    private String country;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
