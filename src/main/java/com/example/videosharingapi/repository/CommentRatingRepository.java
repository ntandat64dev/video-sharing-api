package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.CommentRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRating.CommentRatingId> {

    void deleteByCommentId(String commentId);

    void deleteByCommentParentId(String commentId);

    void deleteByCommentVideoId(String videoId);

    Long countByCommentIdAndRating(String commentId, CommentRating.Rating rating);

    List<CommentRating> findAllByCommentVideoId(String videoId);

    CommentRating findByUserIdAndCommentId(String userId, String commentId);

    Page<CommentRating> findAllByUserIdAndCommentVideoId(String userId, String videoId, Pageable pageable);
}
