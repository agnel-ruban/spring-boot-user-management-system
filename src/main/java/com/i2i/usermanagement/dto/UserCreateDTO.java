package com.i2i.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User creation requests.
 * Contains strict validation annotations for input validation.
 * All fields are required for user creation.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    /**
     * Full name of the user.
     * Must not be null or empty, and should be between 2-50 characters.
     */
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    /**
     * Email address of the user.
     * Must be a valid email format and is required.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    /**
     * Age of the user.
     * Must be between 18 and 120 years and is required.
     */
    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 120, message = "Age must not exceed 120")
    private Integer age;

    /**
     * Phone number of the user.
     * Optional field with validation for phone number format.
     */
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Phone number should be valid")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    private String phoneNumber;

    /**
     * Address of the user.
     * Optional field with maximum length constraint.
     */
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    /**
     * Password for user authentication.
     * Must be between 6 and 255 characters and is required.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    private String password;
}
