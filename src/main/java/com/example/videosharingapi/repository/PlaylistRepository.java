package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, String> {

    @Query(value = "SELECT p FROM Playlist p WHERE p.id IN :ids ORDER BY FIND_IN_SET(p.id, :idsStr)")
    List<Playlist> findAllByIdsAndKeepOrder(List<String> ids, String idsStr);
}
