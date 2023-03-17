package com.whiteboard.service;

import com.whiteboard.dao.model.BoardUserCon;
import com.whiteboard.dao.repository.BoardUserConRepository;
import com.whiteboard.util.DisplayedBoard;
import com.whiteboard.util.UserSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
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
        try {
            boardUserConRepository.save(boardUserCon);
        } catch (DataIntegrityViolationException e) {
            log.info("BoardUserConServiceImpl:: addUserToBoard ignoring PersistenceException");
        }
    }

    public void removeUserFromBoard() {
        BoardUserCon boardUserCon = boardUserConRepository.findByBoardIdAndUserId(
                displayedBoard.getBoard().getId(),
                userSession.getUser().getId()
        );

        boardUserConRepository.deleteById(boardUserCon.getId());
    }
}
