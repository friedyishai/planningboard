package com.whiteboard.rabbitmq;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.UUID;

import static com.whiteboard.constants.Constants.RABBIT_EXCHANGE;

@RequiredArgsConstructor
@EnableRabbit
@Configuration
public class RabbitConfiq {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.port}")
    private Integer connectionTimeout;

    @Value("${spring.rabbitmq.port}")
    private Integer replyTimeout;

    private final String QUEUE_NAME = UUID.randomUUID() + ".queue";

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false, true, true);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(RABBIT_EXCHANGE);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(fanoutExchange());
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setExchange(RABBIT_EXCHANGE);
        return template;
    }

    @Bean
    public ApplicationListener<ContextClosedEvent> contextClosedEventListener(AmqpAdmin amqpAdmin) {
        return event -> {
            amqpAdmin.deleteQueue(QUEUE_NAME);
        };
    }
}
