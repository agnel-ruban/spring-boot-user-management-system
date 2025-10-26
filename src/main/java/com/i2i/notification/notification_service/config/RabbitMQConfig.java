package com.i2i.notification.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for Notification service.
 * Defines queues, exchanges, and routing for receiving user events.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Configuration
public class RabbitMQConfig {

    // Queue names
    public static final String USER_CREATED_QUEUE = "user.created.queue";
    public static final String USER_DELETED_QUEUE = "user.deleted.queue";

    // Exchange name
    public static final String USER_EXCHANGE = "user.exchange";

    // Routing keys
    public static final String USER_CREATED_ROUTING_KEY = "user.created";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";

    /**
     * Message converter for JSON deserialization.
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

    /**
     * User events exchange.
     *
     * @return TopicExchange
     */
    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    /**
     * User created queue.
     *
     * @return Queue
     */
    @Bean
    public Queue userCreatedQueue() {
        return QueueBuilder.durable(USER_CREATED_QUEUE).build();
    }

    /**
     * User deleted queue.
     *
     * @return Queue
     */
    @Bean
    public Queue userDeletedQueue() {
        return QueueBuilder.durable(USER_DELETED_QUEUE).build();
    }

    /**
     * Binding for user created queue.
     *
     * @return Binding
     */
    @Bean
    public Binding userCreatedBinding() {
        return BindingBuilder
                .bind(userCreatedQueue())
                .to(userExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    /**
     * Binding for user deleted queue.
     *
     * @return Binding
     */
    @Bean
    public Binding userDeletedBinding() {
        return BindingBuilder
                .bind(userDeletedQueue())
                .to(userExchange())
                .with(USER_DELETED_ROUTING_KEY);
    }
}
