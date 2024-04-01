package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface HashtagRepository extends JpaRepository<Hashtag, UUID> {

    Hashtag findByTag(String tag);

    @Transactional
    default Hashtag saveIfNotExist(Hashtag hashTag) {
        var savedTag = findByTag(hashTag.getTag());
        if (savedTag != null) return savedTag;
        else return save(hashTag);
    }
}
