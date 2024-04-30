package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Privacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivacyRepository extends JpaRepository<Privacy, String> {
    Privacy findByStatus(Privacy.Status status);
}
