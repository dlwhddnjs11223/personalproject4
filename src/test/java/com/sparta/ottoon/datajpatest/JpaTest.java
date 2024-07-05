package com.sparta.ottoon.datajpatest;

import com.sparta.ottoon.auth.entity.User;
import com.sparta.ottoon.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase
public class JpaTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void test() {
        List<User> users = userRepository.findAll();
        System.out.println(users.get(0));
    }

}
