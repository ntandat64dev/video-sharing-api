package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {

    Hashtag findByTag(String tag);

    @Transactional
    default Hashtag saveIfAbsent(Hashtag hashTag) {
        var savedTag = findByTag(hashTag.getTag());
        if (savedTag != null) return savedTag;
        else return save(hashTag);
    }

    @Query("SELECT h FROM Hashtag h JOIN Video v WHERE v.user.id = :userId")
    List<Hashtag> findAllByUserId(UUID userId);
}
