package com.whiteboard.service.shape;

import com.whiteboard.dao.model.ShapeEntity;

import java.util.List;

public interface ShapeEntityService {

    List<ShapeEntity> getAllShapes(List<Integer> shapesIds);

    ShapeEntity save(ShapeEntity shapeEntity);

    void remove(ShapeEntity shapeEntity);

    ShapeEntity findById(int shapeEntityId);
}
