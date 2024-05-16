package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
public class Hashtag extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 64, nullable = false, unique = true)
    @NonNull
    private String tag;
}
