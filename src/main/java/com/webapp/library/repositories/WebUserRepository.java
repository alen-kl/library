package com.webapp.library.repositories;

import com.webapp.library.data.WebUser;
import org.springframework.data.repository.CrudRepository;

public interface WebUserRepository extends CrudRepository<WebUser,Long> {
    WebUser findByUsernameAndPassword(String username, String password);
    WebUser findByUsername(String username);
}
