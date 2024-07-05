package com.sparta.ottoon.profile.dto;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.like.entity.LikeTypeEnum;
import com.sparta.ottoon.like.entity.Likes;
import com.sparta.ottoon.post.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ProfileResponseDto {
    private String username;
    private String email;
    private String nickname;
    private String intro;
    private String status;
    private int postLikeCount;
    private int commentLikeCount;

    public ProfileResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.intro = user.getIntro();
        this.status = user.getStatus().getStatus();
        this.postLikeCount = user.getLikesList()
                .stream()
                .filter(likes -> likes.getLiketype() == LikeTypeEnum.POST_TYPE)
                .toList()
                .size();
        this.commentLikeCount = user.getLikesList()
                .stream()
                .filter(likes -> likes.getLiketype() == LikeTypeEnum.COMMENT_TYPE)
                .toList()
                .size();
    }
}
