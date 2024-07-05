package com.sparta.ottoon.fixtureMonkey;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.ConstructorPropertiesArbitraryIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FailoverIntrospector;
import com.navercorp.fixturemonkey.api.introspector.FieldReflectionArbitraryIntrospector;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.entity.UserStatus;
import com.sparta.ottoon.comment.entity.Comment;
import com.sparta.ottoon.like.entity.LikeTypeEnum;
import com.sparta.ottoon.like.entity.Likes;
import com.sparta.ottoon.post.entity.Post;
import com.sparta.ottoon.post.entity.PostStatus;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.arbitraries.StringArbitrary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sparta.ottoon.fixtureMonkey.FixtureMonkeyUtil.Entity.toUser;

public class FixtureMonkeyUtil {
    public static FixtureMonkey monkey() {
        return FixtureMonkey.builder()
                .objectIntrospector(new FailoverIntrospector((
                        Arrays.asList(
                                FieldReflectionArbitraryIntrospector.INSTANCE,
                                ConstructorPropertiesArbitraryIntrospector.INSTANCE,
                                BuilderArbitraryIntrospector.INSTANCE
                        )
                )
                ))
                .build();
    }

    public static class Entity {
        public static User toUser() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(User.class)
                    .set("id", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static List<User> toUsers(int count) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(User.class)
                    .set("id", Arbitraries.longs().between(1L, 50L))
                    .set("username", getRandomStringArbitrary(5)
                    )
                    .set("password", Arbitraries.strings()
                            .alpha()
                            .numeric()
                            .withChars('!', '@', '#', '$', '~')
                            .ofMinLength(10)
                    )
                    .set("nickname", "test")
                    .set("email", "test1@test.com")
                    .set("status", UserStatus.ACTIVE)
                    .set("intro", null)
                    .set("likesList", null)
                    .sampleList(count);
        }

        public static Post toPost() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Post.class)
                    .set("id", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }

        public static List<Post> toPosts(int count) {
            return getPostArbitraryBuilder()
                    .sampleList(count);
        }

        public static List<Post> toPosts(int count, List<User> users) {


            return getPostArbitraryBuilder(Arbitraries.longs().between(1L, 50L).sample(), Arbitraries.of(users))
                    .sampleList(count);
        }

        public static List<Likes> toLikes(int count, User user) {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Likes.class)
                    .set("id", Arbitraries.longs().between(1L, 10L))
                    .set("user", user)
                    .set("likeType", LikeTypeEnum.POST_TYPE)
                    .sampleList(count);

        }

        public static Comment toComment() {
            return FixtureMonkeyUtil.monkey()
                    .giveMeBuilder(Comment.class)
                    .set("id", Arbitraries.longs().between(1L, 50L))
                    .sample();
        }
    }

    private static ArbitraryBuilder<Post> getPostArbitraryBuilder() {
        return getPostArbitraryBuilder(Arbitraries.longs().between(1L, 50L).sample(),
                toUser());
    }

    private static ArbitraryBuilder<Post> getPostArbitraryBuilder(Long id, Object user) {
        return FixtureMonkeyUtil.monkey()
                .giveMeBuilder(Post.class)
                .set("id", Arbitraries.longs())
                .set("contents", getRandomStringArbitrary(5, 100))
                .set("postStatus", PostStatus.GENERAL)
                .set("user", user)
                .set("likesList", null);

    }

    public static StringArbitrary getRandomStringArbitrary(int max) {
        return getRandomStringArbitrary(1, max);
    }

    public static StringArbitrary getRandomStringArbitrary(int min, int max) {
        return Arbitraries.strings()
                .alpha()
                .numeric()
                .ofMinLength(min)
                .ofMaxLength(max);
    }
}
