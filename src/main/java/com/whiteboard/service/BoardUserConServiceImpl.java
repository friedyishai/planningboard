package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.BoardUserCon;
import com.whiteboard.dao.repository.BoardUserConRepository;
import com.whiteboard.general.DisplayedBoard;
import com.whiteboard.general.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardUserConServiceImpl implements BoardUserConService {

    private final BoardUserConRepository boardUserConRepository;
    private final UserSession userSession;
    private final DisplayedBoard displayedBoard;

    public void addUserToBoard() {
        BoardUserCon boardUserCon = BoardUserCon.builder()
                .boardId(displayedBoard.getBoard().getId())
                .userId(userSession.getUser().getId())
                .build();

        boardUserConRepository.save(boardUserCon);
    }

    public void removeUserFromBoard() {
        BoardUserCon boardUserCon = BoardUserCon.builder()
                .boardId(displayedBoard.getBoard().getId())
                .userId(userSession.getUser().getId())
                .build();

        boardUserConRepository.delete(boardUserCon);
    }
}
