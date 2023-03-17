package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.MessageBoardUserCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageBoardUserConRepository extends JpaRepository<MessageBoardUserCon, Integer> {

    List<MessageBoardUserCon> findByBoardIdAndCreateDateGreaterThanEqual(
            Integer boardId,
            LocalDate today
    );
}
