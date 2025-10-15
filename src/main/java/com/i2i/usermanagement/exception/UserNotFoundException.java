package com.i2i.usermanagement.exception;

/**
 * Exception thrown when a user is not found in the system.
 * 
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     * 
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

}
