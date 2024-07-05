package com.sparta.ottoon.like.service;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.comment.repository.CommentRepository;
import com.sparta.ottoon.common.exception.CustomException;
import com.sparta.ottoon.common.exception.ErrorCode;
import com.sparta.ottoon.like.entity.Likes;
import com.sparta.ottoon.like.entity.LikeTypeEnum;
import com.sparta.ottoon.like.repository.LikeRepository;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public String postlikeOrUnlike(String username, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        User user = userRepository.findByUsername(username).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(post.getUser().getId() == user.getId()){
            throw new CustomException(ErrorCode.FAIL_LIKESELF);
        }
        Optional<Likes> existingLike = likeRepository.findByPostAndUser(post, user);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return "게시글 좋아요 삭제 완료";
        } else {
            Likes likes = new Likes(user, post, LikeTypeEnum.POST_TYPE);
            likeRepository.save(likes);
            return "게시글 좋아요 완료";
        }
    }
    @Transactional
    public String commentlikeOrUnlike(String username,Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(ErrorCode.FAIL_GETCOMMENT));
        User user = userRepository.findByUsername(username).orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));
        if(comment.getUser().getId() == user.getId()){
            throw new CustomException(ErrorCode.FAIL_COMMENTSELF);
        }
        Optional<Likes> existingLike = likeRepository.findByCommentAndUser(comment, user);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return "댓글 좋아요 삭제 완료";
        } else {
            Likes likes = new Likes(user, comment, LikeTypeEnum.COMMENT_TYPE);
            likeRepository.save(likes);
            return "댓글 좋아요 완료";
        }
    }
    public String getLikeComment(Long commentId){
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new CustomException(ErrorCode.FAIL_GETCOMMENT));
        Long likes = likeRepository.countByComment(comment);
        return "commentid" + comment.getId() + "\n좋아요 :" + likes;
    }

    public String getLikePost(Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomException(ErrorCode.POST_NOT_FOUND));
        Long likes = likeRepository.countByPost(post);
        return "postId" + post.getId() + "\n좋아요 :" + likes;
    }
}
