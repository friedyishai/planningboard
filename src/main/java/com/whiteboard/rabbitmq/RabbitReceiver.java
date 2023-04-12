package com.whiteboard.rabbitmq;

import org.springframework.amqp.core.Message;

public interface RabbitReceiver {

    void receiveMessage(Message message);
}
