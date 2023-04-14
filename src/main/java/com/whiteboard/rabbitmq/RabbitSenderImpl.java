package com.whiteboard.rabbitmq;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import static com.whiteboard.constants.Constants.RABBIT_EXCHANGE;

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitSenderImpl implements RabbitSender {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendMessage(RabbitMessageTypeEnum rabbitMessageTypeEnum) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).putInt(rabbitMessageTypeEnum.ordinal()).array();
        log.info(String.valueOf(rabbitMessageTypeEnum));
        rabbitTemplate.convertAndSend(RABBIT_EXCHANGE, "", bytes);
    }
}

