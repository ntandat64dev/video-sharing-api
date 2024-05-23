package com.example.videosharingapi.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NotificationObject extends AuditableEntity {

    public enum ObjectType {
        VIDEO, FOLLOW, COMMENT;

        @Override
        @JsonValue
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Integer actionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ObjectType objectType;

    private String objectId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "notificationObject", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notification> notifications;
}
