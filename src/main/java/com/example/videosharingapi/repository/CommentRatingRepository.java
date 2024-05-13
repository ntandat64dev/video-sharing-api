package com.example.videosharingapi.repository;

import com.example.videosharingapi.entity.CommentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRating.CommentRatingId> {

    void deleteByCommentVideoId(String videoId);

    Long countByCommentIdAndRating(String commentId, CommentRating.Rating rating);

    List<CommentRating> findAllByCommentVideoId(String videoId);
}
