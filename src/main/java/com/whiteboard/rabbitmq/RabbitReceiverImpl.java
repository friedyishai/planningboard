package com.whiteboard.rabbitmq;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import com.whiteboard.service.board.BoardContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitReceiverImpl implements RabbitReceiver {

    private final BoardContentService boardContentService;

    @Override
    @RabbitListener(queues = "#{@queue.name}")
    public void receiveMessage(Message message) {
        byte[] receivedBytes = message.getBody();
        int ordinal = ByteBuffer.wrap(receivedBytes).getInt();
        RabbitMessageTypeEnum value = RabbitMessageTypeEnum.values()[ordinal];
        boardContentService.setBoardContent(value);
    }
}
