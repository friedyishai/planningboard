package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.repository.BoardRepository;
import com.whiteboard.util.DBActionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.whiteboard.constants.Constants.UNAVAILABLE_BOARD_NAME;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public Board getBoardByName(String boardName) {
        return boardRepository.findByName(boardName);
    }

    @Override
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Override
    public DBActionResult createNewBoard(String boardName) {

        if (!(isBoardNameAvailable(boardName))) {
            return DBActionResult.builder()
                    .isSuccess(false)
                    .failureReason(UNAVAILABLE_BOARD_NAME)
                    .build();
        }

        Board newBoard = Board.builder()
                .name(boardName)
                .isActive(true)
                .build();

        boardRepository.save(newBoard);

        return DBActionResult.builder().isSuccess(true).build();
    }

    private boolean isBoardNameAvailable(String boardName) {
        Board board = boardRepository.findByName(boardName);
        return null == board;
    }
}
