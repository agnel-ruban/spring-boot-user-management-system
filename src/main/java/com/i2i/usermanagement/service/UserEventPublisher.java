package com.i2i.usermanagement.service;

import com.i2i.usermanagement.config.RabbitMQConfig;
import com.i2i.usermanagement.dto.UserEventDTO;
import com.i2i.usermanagement.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for publishing user events to RabbitMQ.
 * Handles sending user lifecycle events to notification service.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Service
public class UserEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(UserEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publishes user created event.
     *
     * @param user         the created user
     * @param userPassword the user's password (plain text)
     */
    public void publishUserCreated(User user, String userPassword) {
        UserEventDTO event = UserEventDTO.builder()
            .eventType("USER_CREATED")
            .userId(user.getId())
            .userName(user.getName())
            .userEmail(user.getEmail())
            .userAge(user.getAge())
            .userPhoneNumber(user.getPhoneNumber())
            .userAddress(user.getAddress())
            .isActive(user.getIsActive())
            .eventTimestamp(LocalDateTime.now())
            .eventData("New user account created")
            .userPassword(userPassword)
            .build();

        publishEvent(event, RabbitMQConfig.USER_CREATED_ROUTING_KEY);
        logger.info("Published USER_CREATED event for user: {}", user.getEmail());
    }

    /**
     * Publishes user deleted event.
     *
     * @param userId    the deleted user ID
     * @param userEmail the deleted user email
     * @param userName  the deleted username
     */
    public void publishUserDeleted(UUID userId, String userEmail, String userName) {
        UserEventDTO event = UserEventDTO.builder()
            .eventType("USER_DELETED")
            .userId(userId)
            .userName(userName)
            .userEmail(userEmail)
            .eventTimestamp(LocalDateTime.now())
            .eventData("User account deleted")
            .build();

        publishEvent(event, RabbitMQConfig.USER_DELETED_ROUTING_KEY);
        logger.info("Published USER_DELETED event for user: {}", userEmail);
    }

    /**
     * Publishes an event to RabbitMQ.
     *
     * @param event      the event to publish
     * @param routingKey the routing key
     */
    private void publishEvent(UserEventDTO event, String routingKey) {
         rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EXCHANGE, routingKey, event);
    }
}
