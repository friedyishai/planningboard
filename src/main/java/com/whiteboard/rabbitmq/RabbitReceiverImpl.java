package com.whiteboard.rabbitmq;

import com.whiteboard.service.BoardContentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.whiteboard.constants.Constants.RABBIT_QUEUE;

@Slf4j
@RequiredArgsConstructor
@Service
public class RabbitReceiverImpl implements RabbitReceiver {

    private final BoardContentService boardContentService;

    @Override
    @RabbitListener(queues = RABBIT_QUEUE)
    public void receiveMessage(Message message) {
        log.info(message.toString());
        boardContentService.setBoardContent();
    }
}
