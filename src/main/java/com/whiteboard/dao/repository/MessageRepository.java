package com.whiteboard.dao.repository;

import com.whiteboard.dao.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {

    List<MessageEntity> findByIdInAndIsActive(List<Integer> messageIds, boolean isActive);
}
