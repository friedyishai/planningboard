package com.whiteboard.dto;

import com.whiteboard.dao.model.User;
import lombok.Data;

@Data
public class UserSession {

    private User user;
}
