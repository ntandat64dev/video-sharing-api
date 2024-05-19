package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {

    @Modifying
    @Query("UPDATE Notification n SET n.isSeen = TRUE WHERE n.recipient.id = :userId")
    void markAllAsSeen(String userId);

    Integer countByRecipientIdAndIsSeenIsFalse(String recipientId);

    Page<Notification> findByRecipientId(String recipientId, Pageable pageable);

    Notification findByIdAndRecipientId(String id, String recipientId);

    Page<Notification> findByNotificationObjectId(String notificationObjectId, Pageable pageable);
}
