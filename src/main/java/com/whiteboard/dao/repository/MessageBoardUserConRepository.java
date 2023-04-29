package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.MessageBoardUserCon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageBoardUserConRepository extends JpaRepository<MessageBoardUserCon, Integer> {

    @Query(value = "SELECT * " +
            "       FROM con_message_to_board_and_user " +
            "       WHERE board_id = :boardId AND :today = CONVERT(date, create_date)", nativeQuery = true)
    List<MessageBoardUserCon> findByBoardIdAndCreateDateGreaterThanEqual(
            Integer boardId,
            LocalDate today
    );
}
