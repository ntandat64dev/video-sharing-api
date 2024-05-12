package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT v.user FROM Video v JOIN v.user WHERE v.id = :videoId")
    User findByVideoId(String videoId);
}
