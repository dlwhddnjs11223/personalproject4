package com.sparta.ottoon.comment.dto;

import com.sparta.ottoon.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long commentId;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int likeCount;

    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.likeCount = comment.getLikesList().size();
    }
}
