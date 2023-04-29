package com.whiteboard.service.text;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.TextBoardCon;
import com.whiteboard.dao.model.TextEntity;
import com.whiteboard.dao.repository.TextBoardConRepository;
import com.whiteboard.singletons.DisplayedBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TextBoardConServiceImpl implements TextBoardConService {

    private final TextBoardConRepository textBoardConRepository;
    private final DisplayedBoard displayedBoard;

    @Override
    public List<Integer> getAllTextIds(Board board) {
        return textBoardConRepository.findByBoardId(board.getId()).stream()
                .map(TextBoardCon::getTextId)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Integer textEntityId) {
        TextBoardCon textBoardCon = TextBoardCon.builder()
                .textId(textEntityId)
                .boardId(displayedBoard.getBoard().getId())
                .build();

        textBoardConRepository.save(textBoardCon);
    }

    @Override
    public void remove(TextEntity textEntity) {
        textBoardConRepository.deleteById(textEntity.getId());
    }
}
