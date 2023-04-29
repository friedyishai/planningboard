package com.whiteboard.service.message;

import com.whiteboard.dao.model.MessageBoardUserCon;
import com.whiteboard.dao.model.MessageEntity;
import com.whiteboard.service.user.UserService;
import com.whiteboard.singletons.DisplayedBoard;
import com.whiteboard.singletons.UserSession;
import lombok.RequiredArgsConstructor;
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
            return buildMessage(username, messageEntity.getContent(), messageEntity.getCreateDate());
        }).collect(Collectors.toList());
    }

    @Override
    public List<String> getMessages() {
        return messages;
    }

    @Override
    public void addMessage(String messageContent, LocalDateTime createDate) {
        String message = buildMessage(userSession.getUser().getName(), messageContent, createDate);
        MessageEntity messageEntity = messageEntityService.save(messageContent);
        messageBoardUserConSerivce.save(messageEntity.getId());
        messages.add(message);
    }

    private String buildMessage(String username, String messageContent, LocalDateTime createDate) {
        String time = getMessageTimeStr(createDate);
        return username + " " + time + ": " + messageContent;
    }

    private String getMessageTimeStr(LocalDateTime createDate) {
        return createDate.getHour() + ":" + createDate.getMinute();
    }
}
