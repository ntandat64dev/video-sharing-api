package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, String> {

    Hashtag findByTag(String tag);

    @Transactional
    default Hashtag saveIfAbsent(Hashtag hashTag) {
        var savedTag = findByTag(hashTag.getTag());
        if (savedTag != null) return savedTag;
        else return save(hashTag);
    }

    @Query("SELECT hashtag FROM Video v JOIN v.hashtags hashtag WHERE v.user.id = :userId")
    List<Hashtag> findAllByUserId(String userId);
}
