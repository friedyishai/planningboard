package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.ShapeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShapeRepository extends JpaRepository<ShapeEntity, Integer> {

    List<ShapeEntity> findByIdInAndIsActive(List<Integer> ids, boolean isActive);
}
