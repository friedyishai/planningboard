package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    Board findByName(String boardName);
}
