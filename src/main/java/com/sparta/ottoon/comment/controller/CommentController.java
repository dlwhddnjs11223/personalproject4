package com.sparta.ottoon.comment.controller;

import com.sparta.ottoon.comment.dto.CommentRequestDto;
import com.sparta.ottoon.comment.dto.CommentResponseDto;
import com.sparta.ottoon.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Comment API", description = "Comment API 입니다")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //comment 생성
    @Operation(summary = "createComment", description = "댓글 생성 기능입니다.")
    @PostMapping("/comments/{postId}")
    public ResponseEntity<CommentResponseDto> createComment(@PathVariable Long postId,
                                                            @RequestBody CommentRequestDto commentRequestDto,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, commentRequestDto, userDetails.getUsername()));
    }

    //comment 조회
    @Operation(summary = "getComment", description = "댓글 조회 기능입니다.")
    @GetMapping("/comment")
    public ResponseEntity<List<CommentResponseDto>> getComment(@PathVariable Long postId
    ) {
        return ResponseEntity.ok().body(commentService.getComment(postId));
    }

    //comment 수정
    @Operation(summary = "updateComment", description = "댓글 수정 기능입니다.")
    @PutMapping("/comment/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(@PathVariable(value = "postId") Long postId, @PathVariable(value = "commentId") Long commentId,
                                                            @RequestBody CommentRequestDto commentRequestDto,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(commentService.updateComment(postId, commentId, commentRequestDto, userDetails.getUsername()));
    }

    //comment 삭제
    @Operation(summary = "deleteComment", description = "댓글 삭제 기능입니다.")
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable(value = "postId") Long postId, @PathVariable(value = "commentId") Long commentId,
                                              @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comments/commentslike")
    public Page<CommentResponseDto> findCommentLike(@AuthenticationPrincipal UserDetails userDetails,
                                                    @RequestParam("page") int page) {
        return commentService.findCommentLike(userDetails.getUsername(), page);

    }

}
