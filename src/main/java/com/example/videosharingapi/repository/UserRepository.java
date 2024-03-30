package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmailAndPassword(String email, String password);

    Boolean existsByEmail(String email);

    User findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);
}
