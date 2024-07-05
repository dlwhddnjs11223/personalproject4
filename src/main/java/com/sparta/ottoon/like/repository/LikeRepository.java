package com.sparta.ottoon.like.repository;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.like.entity.Likes;
import com.sparta.ottoon.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByPostAndUser(Post post, User user);
    Optional<Likes> findByCommentAndUser(Comment comment, User user);

    Long countByPost(Post post);
    Long countByComment(Comment comment);
}

