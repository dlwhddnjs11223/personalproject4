package com.sparta.ottoon.post.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.ottoon.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.ottoon.auth.entity.QUser.user;
import static com.sparta.ottoon.follow.entity.QFollow.follow;
import static com.sparta.ottoon.like.entity.QLikes.likes;
import static com.sparta.ottoon.post.entity.QPost.post;

@RequiredArgsConstructor
@Repository
public class PostCustomRepositoryImpl implements PostCustomRepository {

    public final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<Post> findAllILike(Long userId, Pageable pageable) {
        List<Post> result = jpaQueryFactory.selectFrom(post)
                .leftJoin(post.likesList, likes)
                .fetchJoin()
                .where(likes.user.id.eq(userId))
                .orderBy(likes.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long count = jpaQueryFactory.select(post.count())
                .from(post)
                .leftJoin(post.likesList, likes)
                .where(likes.user.id.eq(userId))
                .fetchOne();


        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<Post> findPostsFollow(Long userId, Pageable pageable) {
        /*
        세타조인
        성능상의 이슈가 있어서 지양해야함.
         */
//        List<Post> postList = jpaQueryFactory.select(post)
//                .from(post, follow)
//                .where(post.user.id.eq(follow.followUser.id))
//                .where(follow.userId.eq(userId).and(follow.isFollow))
//                .fetch();

        List<Post> postList = jpaQueryFactory.select(post)
                .from(post)
                .join(follow).on(post.user.id.eq(follow.followUser.id))
                .fetchJoin()
                .where(follow.userId.eq(userId).and(follow.isFollow))
                .orderBy(post.createdAt.asc(), post.user.username.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = jpaQueryFactory.select(post.count())
                .from(post)
                .join(follow).on(post.user.id.eq(follow.followUser.id))
                .where(follow.userId.eq(userId).and(follow.isFollow))
                .fetchOne();

        return new PageImpl<>(postList, pageable, count);
    }
}

