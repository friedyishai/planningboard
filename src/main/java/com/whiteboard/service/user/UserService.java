package com.whiteboard.service.user;

import com.whiteboard.dao.model.User;
import com.whiteboard.dto.DBActionResult;

public interface UserService {

    DBActionResult login(User user);

    DBActionResult register(User user);

    User findByName(String username);

    String getUserById(Integer uid);

    void logout();

    void saveUser(User user);
}
