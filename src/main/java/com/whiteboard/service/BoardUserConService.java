package com.whiteboard.service;

import com.whiteboard.dao.model.Board;

public interface BoardUserConService {

    void addUserToBoard(Board board);
    void removeUserFromBoard(Board board);
}
