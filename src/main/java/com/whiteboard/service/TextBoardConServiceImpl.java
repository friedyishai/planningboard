package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.TextBoardCon;
import com.whiteboard.dao.repository.TextBoardConRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TextBoardConServiceImpl implements TextBoardConService {

    private final TextBoardConRepository textBoardConRepository;

    @Override
    public List<Integer> getAllTextIds(Board board) {
        return textBoardConRepository.findByBoardId(board.getId()).stream()
                .map(TextBoardCon::getTextId)
                .collect(Collectors.toList());
    }
}
