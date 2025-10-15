package com.i2i.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for User responses.
 * Used for transferring user data to clients after successful operations.
 * Contains no validation annotations as it's for responses only.
 * Excludes sensitive information for security.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDTO {

    /**
     * Unique identifier for the user.
     */
    private UUID id;

    /**
     * Full name of the user.
     */
    private String name;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Age of the user.
     */
    private Integer age;

    /**
     * Phone number of the user.
     */
    private String phoneNumber;

    /**
     * Address of the user.
     */
    private String address;

    /**
     * Indicates whether the user account is active.
     */
    private Boolean isActive;

    /**
     * Timestamp when the user record was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user record was last updated.
     */
    private LocalDateTime updatedAt;
}
