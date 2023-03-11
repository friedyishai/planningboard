package com.whiteboard.service;

import com.whiteboard.dao.model.ShapeEntity;
import com.whiteboard.dao.repository.ShapeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ShapeEntityServiceImpl implements ShapeEntityService {

    private final ShapeRepository shapeRepository;

    public List<ShapeEntity> getAllShapes(List<Integer> shapesIds) {
        return shapeRepository.findByIdInAndIsActive(shapesIds, true);
    }

    @Override
    public ShapeEntity save(ShapeEntity shapeEntity) {
        return shapeRepository.save(shapeEntity);
    }

    @Override
    public void remove(ShapeEntity shapeEntity) {
        shapeEntity.setIsActive(false);
    }
}
