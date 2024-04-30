package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    List<Follow> findAllByUserId(String userId);

    Follow findByFollowerIdAndUserId(String followerId, String userId);

    boolean existsByUserIdAndFollowerId(String userId, String followId);

    long countByUserId(String userId);

    long countByFollowerId(String followerId);
}
