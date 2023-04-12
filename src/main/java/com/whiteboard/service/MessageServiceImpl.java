package com.whiteboard.service;

import com.whiteboard.dao.model.MessageBoardUserCon;
import com.whiteboard.dao.model.MessageEntity;
import com.whiteboard.util.DisplayedBoard;
import com.whiteboard.util.UserSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageBoardUserConSerivce messageBoardUserConSerivce;
    private final MessageEntityService messageEntityService;
    private final UserService userService;
    private final UserSession userSession;
    private final DisplayedBoard displayedBoard;

    private List<String> messages;

    @Override
    public void init() {
        List<MessageBoardUserCon> messageBoardUserCons =
                messageBoardUserConSerivce.getMessageIds(displayedBoard.getBoard());
        Map<Integer, Integer> messageToUserMap = new HashMap<>();
        List<Integer> messageIds = messageBoardUserCons.stream()
                .map(messageBoardUserCon -> {
                    messageToUserMap.put(messageBoardUserCon.getMessageId(), messageBoardUserCon.getUserId());
                    return messageBoardUserCon.getMessageId();
                }).collect(Collectors.toList());
        List<MessageEntity> boardMessages = messageEntityService.getAllMessages(messageIds);
        messages = boardMessages.stream().map(messageEntity -> {
            String username = userService.getUserById(messageToUserMap.get(messageEntity.getId()));
            return buildMessage(username, messageEntity.getContent());
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(String messageContent) {
        String message = buildMessage(userSession.getUser().getName(), messageContent);
        MessageEntity messageEntity = messageEntityService.save(messageContent);
        messageBoardUserConSerivce.save(messageEntity.getId());
        messages.add(message);
    }

    private String buildMessage(String username, String messageContent) {
        String time = getNowTime();
        return username + " " + time + ": " + messageContent;
    }

    private String getNowTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.getHour() + ":" + now.getMinute();
    }
}
