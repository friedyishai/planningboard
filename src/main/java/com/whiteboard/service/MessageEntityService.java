package com.whiteboard.service;

import com.whiteboard.dao.model.MessageEntity;

import java.util.List;

public interface MessageEntityService {

    List<MessageEntity> getAllMessages(List<Integer> messageIds);
}
