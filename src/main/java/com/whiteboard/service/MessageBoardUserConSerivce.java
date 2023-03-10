package com.whiteboard.service;

import com.whiteboard.dao.model.Board;

import java.util.List;

public interface MessageBoardUserConSerivce {

    List<Integer> getMessageIds(Board board);
}
