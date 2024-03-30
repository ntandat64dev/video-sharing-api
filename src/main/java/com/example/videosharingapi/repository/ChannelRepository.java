package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {
    Channel findByUserId(UUID userId);

    @Transactional
    void deleteByUserEmail(String email);
}
