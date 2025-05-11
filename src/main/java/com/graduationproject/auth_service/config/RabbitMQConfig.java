package com.graduationproject.auth_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.user-created}")
    private String queueUserCreated;

    @Value("${rabbitmq.exchange.user-events}")
    private String exchangeUserEvents;

    @Value("${rabbitmq.routing-key.user-created}")
    private String routingKeyUserCreated;

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(queueUserCreated);
    }

    @Bean
    public DirectExchange userEventsExchange() {
        return new DirectExchange(exchangeUserEvents);
    }

    @Bean
    public Binding binding(Queue userCreatedQueue, DirectExchange userEventsExchange) {
        return BindingBuilder.bind(userCreatedQueue)
                .to(userEventsExchange)
                .with(routingKeyUserCreated);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}