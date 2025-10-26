package com.i2i.notification.notification_service.repository;

import com.i2i.notification.notification_service.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Notification entity.
 * Provides database operations for notification tracking.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

}
