package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {

    List<Comment> findByVideoIdAndParentIsNull(String videoId);

    Long countByParentId(String parentId);
}
