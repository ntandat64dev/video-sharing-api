package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.FcmMessageToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FcmMessageTokenRepository extends JpaRepository<FcmMessageToken, String> {

    void deleteAllByTokenIn(List<String> tokens);

    Boolean existsByToken(String token);

    List<FcmMessageToken> findAllByUserIdIn(List<String> userIds);
}
