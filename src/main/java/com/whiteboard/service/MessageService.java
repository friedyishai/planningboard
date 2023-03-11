package com.whiteboard.service;

import java.util.List;

public interface MessageService {

    List<String> getMessages();
    String addMessage(String messageContent);
}
