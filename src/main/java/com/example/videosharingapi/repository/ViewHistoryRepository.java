package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, String> {

    void deleteByVideoId(String videoId);

    List<ViewHistory> findAllByVideoId(String videoId);
}
