package com.i2i.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for user events sent to notification service via RabbitMQ.
 * Contains user information and event metadata.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 25-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventDTO {

    /**
     * Event type (USER_CREATED, USER_DELETED).
     */
    private String eventType;

    /**
     * User ID.
     */
    private UUID userId;

    /**
     * User name.
     */
    private String userName;

    /**
     * User email.
     */
    private String userEmail;

    /**
     * User age.
     */
    private Integer userAge;

    /**
     * User phone number.
     */
    private String userPhoneNumber;

    /**
     * User address.
     */
    private String userAddress;

    /**
     * Whether user is active.
     */
    private Boolean isActive;

    /**
     * Event timestamp.
     */
    private LocalDateTime eventTimestamp;

    /**
     * Additional event data (optional).
     */
    private String eventData;

    /**
     * User password (only for USER_CREATED events).
     */
    private String userPassword;
}
