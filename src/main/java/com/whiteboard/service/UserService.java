package com.whiteboard.service;

import com.whiteboard.dao.model.User;
import com.whiteboard.util.DBActionResult;

public interface UserService {

    DBActionResult login(User user);

    DBActionResult register(User user);

    User findByName(String username);

    String getUserById(Integer uid);

    void logout();

    void saveUser(User user);
}
