package com.i2i.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication requests.
 * Contains username and password for login.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequestDTO {

    /**
     * Username of the user.
     * Must not be blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Password of the user.
     * Must not be blank.
     */
    @NotBlank(message = "Password is required")
    private String password;
}
