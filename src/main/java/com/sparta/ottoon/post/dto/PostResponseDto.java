package com.sparta.ottoon.post.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.ottoon.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostResponseDto {
    private String message;
    private int statusCode;
    private Long postId;
    @NotBlank(message = "내용을 입력해 주세요")
    private String contents;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    //게시글 등록, 수정, 조회
    public static PostResponseDto toDto(String message, int statusCode, Post post) {
        return PostResponseDto.builder()
                .message(message)
                .statusCode(statusCode)
                .postId(post.getId())
                .contents(post.getContents())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    // 게시글 삭제
    public static PostResponseDto toDeleteResponse(String message, int statusCode) {
        return PostResponseDto.builder()
                .message(message)
                .statusCode(statusCode)
                .build();
    }
}
