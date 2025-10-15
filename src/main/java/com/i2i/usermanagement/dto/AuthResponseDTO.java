package com.i2i.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for authentication responses.
 * Contains JWT token and user information.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    /**
     * JWT token for authentication.
     */
    private String token;

    /**
     * Token type (Bearer).
     */
    private String type = "Bearer";

}
