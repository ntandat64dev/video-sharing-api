package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByEmailAndPassword(String email, String password);

    Boolean existsByEmail(String email);

    User findByEmail(String email);
}
