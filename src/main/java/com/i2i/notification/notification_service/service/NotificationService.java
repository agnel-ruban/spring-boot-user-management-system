package com.i2i.notification.notification_service.service;

import com.i2i.notification.notification_service.dto.UserEventDTO;
import com.i2i.notification.notification_service.entity.Notification;
import com.i2i.notification.notification_service.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling notification business logic.
 * Processes user events and manages notification tracking.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    /**
     * Handles user created event.
     *
     * @param userEvent the user event
     */
    public void handleUserCreated(UserEventDTO userEvent) {
        logger.info("Processing USER_CREATED event for user: {}", userEvent.getUserEmail());

        try {
            // Create notification record
            Notification notification = createNotificationRecord(userEvent, Notification.NotificationType.USER_CREATED);

            // Send welcome email
            boolean emailSent = emailService.sendWelcomeEmail(
                userEvent.getUserEmail(),
                userEvent.getUserName(),
                userEvent.getUserPassword()
            );

            // Update notification status
            updateNotificationStatus(notification, emailSent);

            logger.info("User created notification processed for: {}", userEvent.getUserEmail());
        } catch (Exception e) {
            logger.error("Error processing USER_CREATED event for user: {}", userEvent.getUserEmail(), e);
        }
    }

    /**
     * Handles user deleted event.
     *
     * @param userEvent the user event
     */
    public void handleUserDeleted(UserEventDTO userEvent) {
        logger.info("Processing USER_DELETED event for user: {}", userEvent.getUserEmail());

        try {
            // Create notification record
            Notification notification = createNotificationRecord(userEvent, Notification.NotificationType.USER_DELETED);

            // Send account deletion email
            boolean emailSent = emailService.sendAccountDeletionEmail(
                userEvent.getUserEmail(),
                userEvent.getUserName()
            );

            // Update notification status
            updateNotificationStatus(notification, emailSent);

            logger.info("User deleted notification processed for: {}", userEvent.getUserEmail());
        } catch (Exception e) {
            logger.error("Error processing USER_DELETED event for user: {}", userEvent.getUserEmail(), e);
        }
    }

    /**
     * Creates a notification record in the database.
     *
     * @param userEvent the user event
     * @param notificationType the notification type
     * @return the created notification
     */
    private Notification createNotificationRecord(UserEventDTO userEvent, Notification.NotificationType notificationType) {
        String emailSubject = notificationType == Notification.NotificationType.USER_CREATED
            ? "Welcome to Our Platform!"
            : "Account Deleted";

        String emailContent = notificationType == Notification.NotificationType.USER_CREATED
            ? emailService.createWelcomeEmailContent(userEvent.getUserName(), userEvent.getUserEmail(), userEvent.getUserPassword())
            : emailService.createAccountDeletionEmailContent(userEvent.getUserName(), userEvent.getUserEmail());

        Notification notification = Notification.builder()
                .userId(userEvent.getUserId())
                .userEmail(userEvent.getUserEmail())
                .userName(userEvent.getUserName())
                .notificationType(notificationType)
                .emailSubject(emailSubject)
                .emailContent(emailContent)
                .isSent(false)
                .build();

        return notificationRepository.save(notification);
    }

    /**
     * Updates notification status after email sending attempt.
     *
     * @param notification the notification to update
     * @param emailSent    whether the email was sent successfully
     */
    private void updateNotificationStatus(Notification notification, boolean emailSent) {
        notification.setIsSent(emailSent);
        notification.setSentAt(emailSent ? LocalDateTime.now() : null);
        notificationRepository.save(notification);
    }


}
