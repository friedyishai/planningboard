package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.MessageBoardUserCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageBoardUserConRepository extends JpaRepository<MessageBoardUserCon, Integer> {
}
