package com.whiteboard.service.board;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dto.DBActionResult;

import java.util.List;

public interface BoardService {

    Board getBoardByName(String boardName);

    List<Board> getAllBoards();

    DBActionResult createNewBoard(String boardName);
}
