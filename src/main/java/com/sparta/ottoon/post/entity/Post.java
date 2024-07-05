package com.sparta.ottoon.post.entity;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.common.Timestamped;
import com.sparta.ottoon.like.entity.Likes;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PostStatus postStatus;

    @Column(nullable = false)
    private boolean isTop;

    @OneToMany(mappedBy="post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likesList = new ArrayList<>();

    public Post(String contents) {
        this.contents = contents;
        this.postStatus = PostStatus.GENERAL;
    }
    public Post(String contents, User user) {
        this.contents = contents;
        this.user = user;
        this.postStatus = PostStatus.GENERAL;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public void update(String contents) {
        this.contents = contents;
    }

    public void noticed() {
        if (this.getPostStatus().equals(PostStatus.NOTICE)) {
            this.postStatus = PostStatus.GENERAL;
            this.isTop = false;
        } else {
            this.postStatus = PostStatus.NOTICE;
            this.isTop = true;
        }
    }
}