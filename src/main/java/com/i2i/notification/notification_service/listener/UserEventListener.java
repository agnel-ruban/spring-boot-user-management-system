package com.i2i.notification.notification_service.listener;

import com.i2i.notification.notification_service.config.RabbitMQConfig;
import com.i2i.notification.notification_service.dto.UserEventDTO;
import com.i2i.notification.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Message listener for user events from RabbitMQ.
 * Processes user lifecycle events and triggers appropriate notifications.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Component
public class UserEventListener {

    private static final Logger logger = LoggerFactory.getLogger(UserEventListener.class);
    private final NotificationService notificationService;

    public UserEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Listens for user created events.
     *
     * @param userEvent the user event
     */
    @RabbitListener(queues = RabbitMQConfig.USER_CREATED_QUEUE)
    public void handleUserCreated(UserEventDTO userEvent) {
        logger.info("Received USER_CREATED event for user: {}", userEvent.getUserEmail());
        try {
            notificationService.handleUserCreated(userEvent);
        } catch (Exception e) {
            logger.error("Error processing USER_CREATED event for user: {}", userEvent.getUserEmail(), e);
        }
    }

    /**
     * Listens for user deleted events.
     *
     * @param userEvent the user event
     */
    @RabbitListener(queues = RabbitMQConfig.USER_DELETED_QUEUE)
    public void handleUserDeleted(UserEventDTO userEvent) {
        logger.info("Received USER_DELETED event for user: {}", userEvent.getUserEmail());
        try {
            notificationService.handleUserDeleted(userEvent);
        } catch (Exception e) {
            logger.error("Error processing USER_DELETED event for user: {}", userEvent.getUserEmail(), e);
        }
    }
}
