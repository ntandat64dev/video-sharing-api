package com.example.videosharingapi.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Check(constraints = "(is_reply = 1 AND parent_id IS NOT NULL) OR (is_reply = 0 AND parent_id IS NULL)")
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Lob
    @Column(length = 10000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isReply;

    @Column(nullable = false, updatable = false)
    private LocalDateTime commentedAt;

    @ManyToOne
    private Comment parent;

    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Video video;
}
