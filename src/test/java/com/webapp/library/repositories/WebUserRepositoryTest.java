package com.webapp.library.repositories;

import com.webapp.library.data.WebUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WebUserRepositoryTest {

    @Autowired
    private WebUserRepository webUserRepository;

    @Test
    public void testFindAll() {
        List<WebUser> users = (List<WebUser>) webUserRepository.findAll();
        assertTrue(users.size()>0);

    }

}