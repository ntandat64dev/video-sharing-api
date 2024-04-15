package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Privacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PrivacyRepository extends JpaRepository<Privacy, UUID> {
    Privacy findByStatus(Privacy.Status status);
}
