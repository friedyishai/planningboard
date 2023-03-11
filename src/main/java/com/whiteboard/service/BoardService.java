package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.general.DBActionResult;

import java.util.List;

public interface BoardService {

    Board getBoardByName(String boardName);

    List<Board> getAllBoards();

    DBActionResult createNewBoard(String boardName);
}
