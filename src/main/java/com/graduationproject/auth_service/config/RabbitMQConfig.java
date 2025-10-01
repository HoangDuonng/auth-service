package com.graduationproject.auth_service.config;

import com.graduationproject.auth_service.event.UserCreatedEventForAuthService;
import com.graduationproject.common.dto.LoginMessageDTO;
import com.graduationproject.common.dto.LoginResponseMessageDTO;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableRabbit
public class RabbitMQConfig {
    @Value("${rabbitmq.queue.user-created-auth}")
    private String queueUserCreated;

    @Value("${rabbitmq.exchange.user-events}")
    private String exchangeUserEvents;

    @Value("${rabbitmq.routing-key.user-created-auth}")
    private String routingKeyUserCreatedAuth;

    @Value("${rabbitmq.queue.auth-login}")
    private String queueAuthLogin;

    @Value("${rabbitmq.queue.auth-login-response}")
    private String queueAuthLoginResponse;

    @Value("${rabbitmq.exchange.auth-events}")
    private String exchangeAuthEvents;

    @Value("${rabbitmq.routing-key.auth-login}")
    private String routingKeyAuthLogin;

    @Value("${rabbitmq.routing-key.auth-login-response}")
    private String routingKeyAuthLoginResponse;

    @Bean
    public Queue userCreatedQueue() {
        return new Queue(queueUserCreated);
    }

    @Bean
    public Queue authLoginQueue() {
        return new Queue(queueAuthLogin);
    }

    @Bean
    public Queue authLoginResponseQueue() {
        return new Queue(queueAuthLoginResponse);
    }

    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(exchangeUserEvents);
    }

    @Bean
    public DirectExchange authEventsExchange() {
        return new DirectExchange(exchangeAuthEvents);
    }

    @Bean
    public Binding userCreatedBinding(Queue userCreatedQueue, TopicExchange userEventsExchange) {
        return BindingBuilder.bind(userCreatedQueue)
                .to(userEventsExchange)
                .with(routingKeyUserCreatedAuth);
    }

    @Bean
    public Binding authLoginBinding(Queue authLoginQueue, DirectExchange authEventsExchange) {
        return BindingBuilder.bind(authLoginQueue)
                .to(authEventsExchange)
                .with(routingKeyAuthLogin);
    }

    @Bean
    public Binding authLoginResponseBinding(Queue authLoginResponseQueue, DirectExchange authEventsExchange) {
        return BindingBuilder.bind(authLoginResponseQueue)
                .to(authEventsExchange)
                .with(routingKeyAuthLoginResponse);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("LoginResponseMessageDTO", LoginResponseMessageDTO.class);
        idClassMapping.put("LoginMessageDTO", LoginMessageDTO.class);
        idClassMapping.put("com.graduationproject.user_service.event.UserCreatedEventForAuthService", UserCreatedEventForAuthService.class);
        typeMapper.setIdClassMapping(idClassMapping);
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.addTrustedPackages("com.graduationproject.user_service.event");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

}
