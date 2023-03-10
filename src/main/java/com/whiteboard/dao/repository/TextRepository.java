package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TextRepository extends JpaRepository<TextEntity, Integer> {

    List<TextEntity> findByIdInAndIsActive(List<Integer> ids, boolean isActive);
}
