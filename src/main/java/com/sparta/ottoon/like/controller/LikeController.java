package com.sparta.ottoon.like.controller;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.service.UserService;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.comment.repository.CommentRepository;
import com.sparta.ottoon.like.entity.LikeTypeEnum;
import com.sparta.ottoon.like.service.LikeService;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.repository.PostRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "createLikePost", description = "게시글 좋아요 생성 기능")
    @PostMapping("/post/{postId}/like")
    public ResponseEntity<String> likeOrUnlikePost(@AuthenticationPrincipal UserDetails userDetails,
                                                   @PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.postlikeOrUnlike(userDetails.getUsername(), postId));
    }

    @Operation(summary = "createLikeComment", description = "댓글 좋아요 생성 기능")
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<String> likeOrUnlikeComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long commentId) {
        return ResponseEntity.status(HttpStatus.OK).body(likeService.commentlikeOrUnlike(userDetails.getUsername() , commentId));
    }

    @Operation(summary = "getLikeComment", description = "댓글 좋아요 조회 기능")
    @GetMapping("/comment/{commentId}/like")
    public ResponseEntity<String> getLikeComment(@PathVariable Long commentId){
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikeComment(commentId));
    }

    @Operation(summary = "getLikePost", description = "게시글 좋아요 조회 기능")
    @GetMapping("/post/{postId}/like")
    public ResponseEntity<String> getLikePost(@PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(likeService.getLikePost(postId));
    }
}
