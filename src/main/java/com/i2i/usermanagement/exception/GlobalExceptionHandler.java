package com.i2i.usermanagement.exception;

import com.i2i.usermanagement.dto.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the application.
 * Handles all exceptions and provides consistent error responses.
 *
 * @author Agnel Ruban
 * @version 1.0
 * @since 13-10-2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles UserNotFoundException.
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 404 status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleUserNotFoundException(UserNotFoundException exception,
                                                               HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(exception.getMessage())
                .errorCode("USER_NOT_FOUND")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    /**
     * Handles UserAlreadyExistsException.
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 409 status
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorDTO> handleUserAlreadyExistsException(UserAlreadyExistsException exception,
                                                                    HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .errorCode("USER_ALREADY_EXISTS")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
    }


    /**
     * Handles MethodArgumentNotValidException (validation errors).
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 400 status and validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationException(MethodArgumentNotValidException exception,
                                                             HttpServletRequest request) {
        // Get the first validation error message
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Validation failed");

        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .errorCode("VALIDATION_ERROR")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles MethodArgumentTypeMismatchException (invalid parameter types).
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorDTO> handleTypeMismatchException(MethodArgumentTypeMismatchException exception,
                                                               HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Invalid parameter format")
                .errorCode("INVALID_PARAMETER_FORMAT")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles AuthenticationException.
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 401 status
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDTO> handleAuthenticationException(AuthenticationException exception, HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(exception.getMessage())
                .errorCode("AUTHENTICATION_FAILED")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorDTO);
    }

    /**
     * Handles AccessDeniedException (authorization failures).
     *
     * @param exception the exception
     * @param request the HTTP request
     * @return ErrorDTO with 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDTO> handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(exception.getMessage())
                .errorCode("ACCESS_DENIED")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorDTO);
    }


    /**
     * Handles all other exceptions.
     * @param request the HTTP request
     * @return ErrorDTO with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception exception, HttpServletRequest request) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .errorCode("INTERNAL_SERVER_ERROR")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }
}
