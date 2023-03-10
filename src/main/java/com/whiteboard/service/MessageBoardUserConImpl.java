package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.MessageBoardUserCon;
import com.whiteboard.dao.repository.MessageBoardUserConRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageBoardUserConImpl implements MessageBoardUserConSerivce {

    private final MessageBoardUserConRepository messageBoardUserConRepository;

    public List<Integer> getMessageIds(Board board) {
        return messageBoardUserConRepository.
                findByBoardIdAndByCreateDateGreaterThanEqual(board.getId(), LocalDate.now())
                .stream()
                .map(MessageBoardUserCon::getMessageId)
                .collect(Collectors.toList());
    }
}
