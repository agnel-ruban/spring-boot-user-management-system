package com.i2i.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple Data Transfer Object for error responses.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    /**
     * HTTP status code of the error response.
     */
    private int status;

    /**
     * Error message describing what went wrong.
     */
    private String message;

    /**
     * Error code for programmatic handling.
     */
    private String errorCode;

    /**
     * Path of the request that caused the error.
     */
    private String path;
}
