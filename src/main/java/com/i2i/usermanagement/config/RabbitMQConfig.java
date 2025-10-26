package com.i2i.usermanagement.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ's configuration for User Management service.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Configuration
public class RabbitMQConfig {

    public static final String USER_EXCHANGE = "user.exchange";

    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    /**
     * Message converter for JSON serialization.
     *
     * @return Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate with JSON message converter.
     *
     * @param connectionFactory the connection factory
     * @return RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
