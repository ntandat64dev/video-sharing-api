package com.example.videosharingapi.repository;

import com.example.videosharingapi.model.entity.CommentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRating.CommentRatingId> {
}
