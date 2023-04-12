package com.whiteboard.rabbitmq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static com.whiteboard.constants.Constants.RABBIT_EXCHANGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitSenderImpl implements RabbitSender {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMessage(Message message) {
        log.info(message.toString());
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE, "", message);
    }
}
