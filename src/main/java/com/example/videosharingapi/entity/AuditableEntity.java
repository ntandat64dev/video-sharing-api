package com.example.videosharingapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity implements Serializable {

    @CreatedBy
    @Column(length = 64, nullable = false)
    protected String createdBy;

    @CreatedDate
    @Column(nullable = false)
    protected LocalDateTime createdDate;

    @LastModifiedBy
    @Column(length = 64, nullable = false)
    protected String modifiedBy;

    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedDate;
}
