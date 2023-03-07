package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.ShapeBoardCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShapeBoardConRepository extends JpaRepository<ShapeBoardCon, Integer> {
}
