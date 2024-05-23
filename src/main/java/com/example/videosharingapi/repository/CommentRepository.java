package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    Long deleteByParentId(String parentId);

    void deleteByVideoId(String videoId);

    Page<Comment> findAllByVideoId(String videoId, Pageable pageable);

    Page<Comment> findAllByVideoIdAndParentIsNull(String videoId, Pageable pageable);

    Page<Comment> findAllByParentId(String parentId, Pageable pageable);

    Long countByParentId(String parentId);
}
