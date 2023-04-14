package com.whiteboard.rabbitmq;

import com.whiteboard.enums.RabbitMessageTypeEnum;
import org.springframework.amqp.core.Message;

import java.io.IOException;

public interface RabbitSender {

    void sendMessage(RabbitMessageTypeEnum rabbitMessageTypeEnum) throws IOException;
}
