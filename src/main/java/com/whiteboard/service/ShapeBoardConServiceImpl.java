package com.whiteboard.service;

import com.whiteboard.dao.model.Board;
import com.whiteboard.dao.model.ShapeBoardCon;
import com.whiteboard.dao.repository.ShapeBoardConRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ShapeBoardConServiceImpl implements ShapeBoardConService {

    private final ShapeBoardConRepository shapeBoardConRepository;

    @Override
    public List<Integer> getAllShapeIds(Board board) {
        return shapeBoardConRepository.findByBoardId(board.getId()).stream()
                .map(ShapeBoardCon::getShapeId)
                .collect(Collectors.toList());
    }
}
