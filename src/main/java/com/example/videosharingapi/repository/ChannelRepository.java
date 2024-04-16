package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    @Query("SELECT c FROM User u JOIN u.channel c WHERE u.id = :userId")
    Channel findByUserId(UUID userId);
}
