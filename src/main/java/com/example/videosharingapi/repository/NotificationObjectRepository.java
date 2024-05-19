package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.NotificationObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationObjectRepository extends JpaRepository<NotificationObject, String> {

    Optional<NotificationObject> findByObjectId(String objectId);
}
