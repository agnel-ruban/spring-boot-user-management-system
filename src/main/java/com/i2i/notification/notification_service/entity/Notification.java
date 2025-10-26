package com.i2i.notification.notification_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity to track email notifications sent to users.
 * Stores details about notification delivery status and content.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Entity
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    /**
     * Unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * User ID from the user management service.
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * User email address.
     */
    @Column(name = "user_email", nullable = false, length = 100)
    private String userEmail;

    /**
     * User name.
     */
    @Column(name = "user_name", length = 50)
    private String userName;

    /**
     * Type of notification (USER_CREATED, USER_DELETED).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationType notificationType;

    /**
     * Email subject.
     */
    @Column(name = "email_subject", nullable = false, length = 200)
    private String emailSubject;

    /**
     * Email content/body.
     */
    @Column(name = "email_content", columnDefinition = "TEXT")
    private String emailContent;

    /**
     * Whether the email was sent successfully.
     */
    @Builder.Default
    @Column(name = "is_sent", nullable = false)
    private Boolean isSent = false;

    /**
     * Timestamp when the notification was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the notification was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Timestamp when the email was sent (if successful).
     */
    @Column(name = "sent_at")
    private LocalDateTime sentAt;


    /**
     * Enum for notification types.
     */
    public enum NotificationType {
        USER_CREATED,
        USER_DELETED
    }
}
