package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.VideoStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoStatisticRepository extends JpaRepository<VideoStatistic, UUID> {

    @Query("""
            SELECT SUM(vs.viewCount)
            FROM VideoStatistic vs JOIN Video v ON vs.id = v.id JOIN User u ON v.user.id = u.id
            WHERE u.id = :userId
            GROUP BY u.id""")
    Long sumViewCountByUserId(UUID userId);
}
