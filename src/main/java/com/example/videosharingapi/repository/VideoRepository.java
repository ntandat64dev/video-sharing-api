package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    Page<Video> findAllByUserId(String userId, Pageable pageable);

    Long countByUserId(String userId);

    @Query(
            value = """
                    SELECT * FROM video WHERE user_id IN\s
                    (SELECT user_id FROM follow WHERE follower_id = :userId)""",
            countQuery = """
                    SELECT COUNT(*) FROM video WHERE user_id IN\s
                    (SELECT user_id FROM follow WHERE follower_id = :userId)""",
            nativeQuery = true)
    Page<Video> findFollowingVideos(String userId, Pageable pageable);

    @Query(value = "SELECT v FROM Video v WHERE v.id IN :ids ORDER BY FIND_IN_SET(v.id, :idsStr)")
    List<Video> findAllByIdsAndKeepOrder(List<String> ids, String idsStr);
}
