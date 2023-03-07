package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.TextBoardCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TextBoardConRepository extends JpaRepository<TextBoardCon, Integer> {
}
