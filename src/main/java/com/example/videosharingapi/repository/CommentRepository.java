package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    void deleteByVideoId(String videoId);

    Page<Comment> findAllByVideoId(String videoId, Pageable pageable);

    Page<Comment> findByVideoIdAndParentIsNull(String videoId, Pageable pageable);

    Long countByParentId(String parentId);
}
