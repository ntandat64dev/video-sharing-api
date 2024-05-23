package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Follow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, String> {

    Page<Follow> findAllByFollowerId(String followerId, Pageable pageable);

    Page<Follow> findAllByUserId(String userId, Pageable pageable);

    Follow findByUserIdAndFollowerId(String userId, String followerId);

    Boolean existsByUserIdAndFollowerId(String userId, String followId);

    Long countByUserId(String userId);

    Long countByFollowerId(String followerId);
}
