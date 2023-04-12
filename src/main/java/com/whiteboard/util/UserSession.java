package com.whiteboard.util;

import com.whiteboard.dao.model.User;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Data
@Component
public class UserSession {

    private User user;
}
