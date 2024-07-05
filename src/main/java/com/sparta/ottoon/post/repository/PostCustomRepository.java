package com.sparta.ottoon.post.repository;

import com.sparta.ottoon.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface PostCustomRepository {

    Page<Post> findAllILike(Long userId, Pageable pageable );

    Page<Post> findPostsFollow(Long userId,Pageable pageable);

}
