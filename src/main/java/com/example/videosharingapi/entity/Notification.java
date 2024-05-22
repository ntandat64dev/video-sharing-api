package com.example.videosharingapi.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Notification extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private NotificationObject notificationObject;

    @ManyToOne
    private User actor;

    @ManyToOne
    private User recipient;

    @Column(nullable = false)
    private Boolean isSeen;

    @Column(nullable = false)
    private Boolean isRead;
}
