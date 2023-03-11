package com.whiteboard.service;

import com.whiteboard.dao.model.User;
import com.whiteboard.general.DBActionResult;

public interface UserService {

    DBActionResult login(User user);

    DBActionResult register(User user);

    User findByName(String username);
}
