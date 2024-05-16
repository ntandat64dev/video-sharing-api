package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    Page<Follow> findAllByFollowerId(String followerId, Pageable pageable);

    Follow findByUserIdAndFollowerId(String userId, String followerId);

    Boolean existsByUserIdAndFollowerId(String userId, String followId);

    Long countByUserId(String userId);

    Long countByFollowerId(String followerId);
}
