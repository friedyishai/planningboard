package com.whiteboard.service.message;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.MessageBoardUserCon;

import java.util.List;

public interface MessageBoardUserConSerivce {

    List<MessageBoardUserCon> getMessageIds(Board board);

    MessageBoardUserCon save(Integer id);
}
