package com.whiteboard.service;

import com.whiteboard.dao.model.MessageEntity;
import com.whiteboard.dao.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageEntityServiceImpl implements MessageEntityService {

    private final MessageRepository messageRepository;

    public List<MessageEntity> getAllMessages(List<Integer> messageIds) {
        return messageRepository.findByIdInAndIsActive(messageIds, true);
    }
}
