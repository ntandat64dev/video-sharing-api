package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VisibilityRepository extends JpaRepository<Visibility, UUID> {
    Visibility findByLevel(Visibility.VisibilityLevel level);
}
