package com.whiteboard.service.message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageService {

    List<String> getMessages();

    void addMessage(String messageContent, LocalDateTime createDate);

    void init();
}
