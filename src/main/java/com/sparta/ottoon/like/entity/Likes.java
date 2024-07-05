package com.sparta.ottoon.like.entity;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.common.Timestamped;
import com.sparta.ottoon.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "likes")
@NoArgsConstructor
public class Likes extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "like_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LikeTypeEnum liketype;


    public Likes(User user, Post post, LikeTypeEnum liketype) {
        this.user = user;
        this.post = post;
        this.liketype = liketype;
        this.post.getLikesList().add(this);
        this.user.getLikesList().add(this);
            }

    public Likes(User user, Comment comment, LikeTypeEnum liketype) {
        this.user = user;
        this.comment = comment;
        this.liketype = liketype;
        this.comment.getLikesList().add(this);
        this.user.getLikesList().add(this);
    }
}
