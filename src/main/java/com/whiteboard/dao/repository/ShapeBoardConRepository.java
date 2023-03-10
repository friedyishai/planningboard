package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.ShapeBoardCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShapeBoardConRepository extends JpaRepository<ShapeBoardCon, Integer> {

    List<ShapeBoardCon> findByBoardId(Integer bordId);
}
