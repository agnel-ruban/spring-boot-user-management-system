package com.i2i.usermanagement.exception;

/**
 * Exception thrown when authentication fails.
 * This exception is used for invalid username/password scenarios.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 14-10-2025
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }

}
