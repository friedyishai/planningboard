package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;

import java.util.List;

public interface ShapeEntityService {

    List<ShapeEntity> getAllShapes(List<Integer> shapesIds);

    ShapeEntity save(ShapeEntity shapeEntity);

    void remove(ShapeEntity shapeEntity);
}
