package com.whiteboard.service.shape;

import com.whiteboard.dao.model.Board;

import java.util.List;

public interface ShapeBoardConService {

    List<Integer> getAllShapeIds(Board board);

    void save(Integer shapeEntityId);

    void remove(Integer shapeEntityId);
}
