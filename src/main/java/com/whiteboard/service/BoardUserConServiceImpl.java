package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.BoardUserCon;
import com.whiteboard.dao.repository.BoardUserConRepository;
import com.whiteboard.dto.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BoardUserConServiceImpl implements BoardUserConService {

    private final BoardUserConRepository boardUserConRepository;
    private final UserSession userSession;

    public void addUserToBoard(Board board) {
        BoardUserCon boardUserCon = BoardUserCon.builder()
                .boardId(board.getId())
                .userId(userSession.getUser().getId())
                .build();

        boardUserConRepository.save(boardUserCon);
    }

    public void removeUserFromBoard(Board board) {
        BoardUserCon boardUserCon = BoardUserCon.builder()
                .boardId(board.getId())
                .userId(userSession.getUser().getId())
                .build();

        boardUserConRepository.delete(boardUserCon);
    }
}
