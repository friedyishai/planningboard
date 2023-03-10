package com.whiteboard.service;

import com.whiteboard.dao.model.User;
import com.whiteboard.dto.DBActionResult;

public interface UserService {

    DBActionResult login(User user);

    DBActionResult register(User user);

    User findByName(String username);
}
