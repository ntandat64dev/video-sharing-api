package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, UUID> {

    List<Follow> findAllByUserId(UUID userId);

    Follow findByFollowerIdAndUserId(UUID followerId, UUID userId);

    boolean existsByUserIdAndFollowerId(UUID userId, UUID followId);

    long countByUserId(UUID userId);

    long countByFollowerId(UUID followerId);
}
