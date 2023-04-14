package com.whiteboard.rabbitmq;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import com.whiteboard.service.BoardContentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.UUID;

import static com.whiteboard.constants.Constants.RABBIT_EXCHANGE;
import static com.whiteboard.constants.Constants.RABBIT_QUEUE;

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
