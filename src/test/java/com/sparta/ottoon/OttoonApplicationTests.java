package com.sparta.ottoon;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import com.sparta.ottoon.fixtureMonkey.FixtureMonkeyUtil;
import com.sparta.ottoon.post.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class OttoonApplicationTests {

    @Autowired
    private UserRepository userRepository;
    protected List<User> users;
    protected List<User> userDataInit() {
        List<User> users = FixtureMonkeyUtil.Entity.toUsers(5);
        this.users = userRepository.saveAll(users);
        return this.users;
    }

    protected List<Post> getPostDataInit() {
        return getPostDataInit(5);
    }

    protected List<Post> getPostDataInit(int count) {
        userDataInit();
        List<Post> posts = FixtureMonkeyUtil.Entity.toPosts(count, this.users);
        return posts;
    }
}
