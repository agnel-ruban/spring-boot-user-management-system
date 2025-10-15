package com.i2i.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User update requests.
 * Contains flexible validation annotations for partial updates.
 * All fields are optional for partial updates.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    /**
     * Full name of the user.
     * Optional field for updates.
     */
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    /**
     * Email address of the user.
     * Optional field for updates.
     */
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    /**
     * Age of the user.
     * Optional field for updates.
     */
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;

    /**
     * Phone number of the user.
     * Optional field for updates.
     */
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    /**
     * Address of the user.
     * Optional field for updates.
     */
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * Indicates whether the user account is active.
     * Optional field for updates (useful for activation/deactivation).
     */
    private Boolean isActive;

    /**
     * Password for user authentication.
     * Optional field for updates.
     */
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;
}
