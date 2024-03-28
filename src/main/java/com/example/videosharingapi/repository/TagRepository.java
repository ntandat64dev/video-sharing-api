package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {

    Tag findByTag(String tag);

    @Transactional
    default Tag saveIfNotExist(Tag tag) {
        var savedTag = findByTag(tag.getTag());
        if (savedTag != null) return savedTag;
        else return save(tag);
    }
}
