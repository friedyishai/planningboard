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
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("prototype")
@Lazy
public class MessageServiceImpl implements MessageService {

    private final MessageBoardUserConSerivce messageBoardUserConSerivce;
    private final MessageEntityService messageEntityService;
    private final UserSession userSession;
    private final DisplayedBoard displayedBoard;

    private List<String> messages;

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public String addMessage(String messageContent) {
        String message = buildMessage(messageContent);
        MessageEntity messageEntity = messageEntityService.save(messageContent);
        messageBoardUserConSerivce.save(messageEntity.getId());
        messages.add(message);
        return message;
    }

    @PostConstruct
    private void init() {
        List<MessageBoardUserCon> messageBoardUserCons =
                messageBoardUserConSerivce.getMessageIds(displayedBoard.getBoard());
        List<Integer> messageIds = messageBoardUserCons.stream()
                .map(MessageBoardUserCon::getMessageId).collect(Collectors.toList());
        List<MessageEntity> boardMessages = messageEntityService.getAllMessages(messageIds);
        messages = boardMessages.stream().map(MessageEntity::getContent).collect(Collectors.toList());
    }

    private String buildMessage(String messageContent) {
        String username = userSession.getUser().getName();
        String time = getNowTime();
        return username + " " + time + ": " + messageContent;
    }

    private String getNowTime() {
        LocalDateTime now = LocalDateTime.now();
        return now.getHour() + ":" + now.getMinute();
    }
}
