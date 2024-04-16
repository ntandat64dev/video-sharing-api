package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, UUID> {
}
