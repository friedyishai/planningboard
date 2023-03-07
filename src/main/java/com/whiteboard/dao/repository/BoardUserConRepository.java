package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.BoardUserCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardUserConRepository extends JpaRepository<BoardUserCon, Integer> {
}
