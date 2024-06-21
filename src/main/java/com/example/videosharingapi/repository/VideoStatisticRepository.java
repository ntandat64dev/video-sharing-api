package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.VideoStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoStatisticRepository extends JpaRepository<VideoStatistic, String> {

    @Query("""
            SELECT COALESCE(SUM(vs.viewCount), 0)
            FROM VideoStatistic vs JOIN Video v ON vs.id = v.id JOIN User u ON v.user.id = u.id
            WHERE u.id = :userId""")
    Long sumViewCountByUserId(String userId);

    @Query("SELECT vs FROM VideoStatistic vs JOIN Comment c ON c.video.id = vs.id WHERE c.id = :commentId")
    VideoStatistic findByCommentId(String commentId);
}
