package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    User findByUsername(String username);

    Boolean existsByUsername(String username);

    @Query("SELECT v.user FROM Video v JOIN v.user WHERE v.id = :videoId")
    User findByVideoId(String videoId);

    @Query(value = "SELECT u FROM User u WHERE u.id IN :ids ORDER BY FIND_IN_SET(u.id, :idsStr)")
    List<User> findAllByIdsAndKeepOrder(List<String> ids, String idsStr);
}
