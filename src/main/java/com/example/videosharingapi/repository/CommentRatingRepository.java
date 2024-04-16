package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.CommentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRating.CommentRatingId> {

    CommentRating findByUserIdAndCommentId(UUID userId, UUID commentId);

    long countByCommentIdAndRating(UUID commentId, CommentRating.Rating rating);
}
