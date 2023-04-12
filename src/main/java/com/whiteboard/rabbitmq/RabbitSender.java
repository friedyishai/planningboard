package com.whiteboard.rabbitmq;

import org.springframework.amqp.core.Message;

public interface RabbitSender {

    void sendMessage(Message message);
}
