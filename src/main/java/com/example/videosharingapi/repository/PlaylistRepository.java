package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    @Query("""
            SELECT p FROM Playlist p WHERE p.user.id = :userId ORDER BY
                CASE
                    WHEN p.defaultType = 0 THEN 0
                    WHEN p.defaultType = 1 THEN 1
                    ELSE 2
                END
            """)
    Page<Playlist> findAllByUserId(String userId, Pageable pageable);

    List<Playlist> findAllByUserIdAndDefaultTypeIsNotNull(String userId);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND p.defaultType = 1")
    Playlist findLikedVideosPlaylistByUserId(String userId);

    @Query(value = "SELECT p FROM Playlist p WHERE p.id IN :ids ORDER BY FIND_IN_SET(p.id, :idsStr)")
    List<Playlist> findAllByIdsAndKeepOrder(List<String> ids, String idsStr);

    @Query("""
            SELECT DISTINCT p.id FROM Playlist p JOIN PlaylistItem pi ON p.id = pi.playlist.id
                WHERE pi.video.id = :videoId AND p.user.id = :userId AND
                    (p.defaultType IS NULL OR p.defaultType NOT IN :excludeDefaults)
            """)
    List<String> findPlaylistIdsContainingVideo(String videoId, String userId, List<Integer> excludeDefaults);
}
