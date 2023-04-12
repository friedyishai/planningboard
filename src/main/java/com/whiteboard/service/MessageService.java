package com.whiteboard.service;

import java.util.List;

public interface MessageService {

    List<String> getMessages();

    void addMessage(String messageContent);

    void init();
}
