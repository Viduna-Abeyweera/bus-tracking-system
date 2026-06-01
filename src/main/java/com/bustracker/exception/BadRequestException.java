package com.bustracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client request contains invalid data
 * that does not pass business-level validation.
 *
 * <p>This is distinct from Bean Validation errors (which are handled
 * separately via {@code MethodArgumentNotValidException}). Use this
 * exception for custom business rules, such as duplicate entries
 * or invalid state transitions.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with a descriptive message.
     *
     * @param message explanation of why the request is invalid
     */
    public BadRequestException(String message) {
        super(message);
    }
}
