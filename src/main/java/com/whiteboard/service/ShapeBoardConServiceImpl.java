package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.ShapeBoardCon;
import com.whiteboard.dao.repository.ShapeBoardConRepository;
import com.whiteboard.util.DisplayedBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShapeBoardConServiceImpl implements ShapeBoardConService {

    private final ShapeBoardConRepository shapeBoardConRepository;
    private final DisplayedBoard displayedBoard;

    @Override
    public List<Integer> getAllShapeIds(Board board) {
        return shapeBoardConRepository.findByBoardId(board.getId()).stream()
                .map(ShapeBoardCon::getShapeId)
                .collect(Collectors.toList());
    }

    @Override
    public void save(Integer shapeEntityId) {
        ShapeBoardCon shapeBoardCon = ShapeBoardCon.builder()
                .shapeId(shapeEntityId)
                .boardId(displayedBoard.getBoard().getId())
                .build();

        shapeBoardConRepository.save(shapeBoardCon);
    }

    @Override
    public void remove(Integer shapeEntityId) {
        shapeBoardConRepository.deleteById(shapeEntityId);
    }
}
