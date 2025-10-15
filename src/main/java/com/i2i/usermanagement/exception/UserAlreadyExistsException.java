package com.i2i.usermanagement.exception;

/**
 * Exception thrown when attempting to create a user that already exists.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
public class UserAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new UserAlreadyExistsException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
