package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.MessageBoardUserCon;
import com.whiteboard.dao.repository.MessageBoardUserConRepository;
import com.whiteboard.util.DisplayedBoard;
import com.whiteboard.util.UserSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageBoardUserConServiceImpl implements MessageBoardUserConSerivce {

    private final MessageBoardUserConRepository messageBoardUserConRepository;
    private final UserSession userSession;
    private final DisplayedBoard displayedBoard;

    public List<MessageBoardUserCon> getMessageIds(Board board) {
        return messageBoardUserConRepository.
                findByBoardIdAndCreateDateGreaterThanEqual(board.getId(), LocalDate.now());
    }

    @Override
    public MessageBoardUserCon save(Integer messageId) {
        MessageBoardUserCon messageBoardUserCon = MessageBoardUserCon.builder()
                .messageId(messageId)
                .boardId(displayedBoard.getBoard().getId())
                .userId(userSession.getUser().getId())
                .createDate(LocalDate.now())
                .build();

        return messageBoardUserConRepository.saveAndFlush(messageBoardUserCon);
    }
}
